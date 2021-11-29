/*
 *
 *  Copyright 2015-2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.spring.web.plugins;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spring.web.OnServletBasedWebApplication;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;
import static springfox.documentation.spring.web.paths.Paths.ROOT;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Conditional(OnServletBasedWebApplication.class)
public class WebMvcRequestHandlerProvider implements RequestHandlerProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcRequestHandlerProvider.class);
    private final List<RequestMappingInfoHandlerMapping> handlerMappings;
    private final HandlerMethodResolver methodResolver;
    private final String contextPath;

    @Autowired
    public WebMvcRequestHandlerProvider(
            Optional<ServletContext> servletContext,
            HandlerMethodResolver methodResolver,
            List<RequestMappingInfoHandlerMapping> handlerMappings) {
        // this.handlerMappings = handlerMappings;
        // mapping.getPatternParser() Return the configured PathPatternParser, or null。把无PatternsRequestCondition的过滤掉了。
        var warring = """    
                  The actual matchingStrategy of the bean [{}] is [PathPatternParser], which is not support by springfox, ignored. 
                    It contains the following patterns [{}].
                    Notice that you have to set `spring.mvc.pathmatch.matching-strategy=ant-path-matcher` in the configuration. 
                  see: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes  
                """;
        this.handlerMappings = handlerMappings.stream().filter(mapping -> {
            PathPatternParser patternParser = mapping.getPatternParser();
            if (!Objects.isNull(patternParser)) {
                String beanName = mapping.getClass().getSimpleName();
                String patterns = mapping.getHandlerMethods().keySet().stream()
                        .flatMap(requestMappingInfo -> requestMappingInfo.getPathPatternsCondition() != null ?
                                requestMappingInfo.getPathPatternsCondition().getDirectPaths().stream() : Stream.empty())
                        .collect(joining(","));
                LOGGER.warn(warring, beanName, patterns);
            }
            return Objects.isNull(patternParser);
        }).toList();
        this.methodResolver = methodResolver;
        this.contextPath = servletContext
                .map(ServletContext::getContextPath)
                .orElse(ROOT);
    }

    @Override
    public List<RequestHandler> requestHandlers() {
        return nullToEmptyList(handlerMappings).stream()
                // .filter(requestMappingInfoHandlerMapping ->
                //         !( requestMappingInfoHandlerMapping instanceof  org.springframework.integration.http.inbound.IntegrationRequestMappingHandlerMapping))
                .map(toMappingEntries())
                .flatMap((entries -> StreamSupport.stream(entries.spliterator(), false)))
                .map(toRequestHandler())
                .sorted(byPatternsCondition())
                .toList();
    }

    private Function<RequestMappingInfoHandlerMapping,
            Iterable<Map.Entry<RequestMappingInfo, HandlerMethod>>> toMappingEntries() {
        return input -> input.getHandlerMethods()
                .entrySet();
    }

    private Function<Map.Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
        return input -> new WebMvcRequestHandler(
                contextPath,
                methodResolver,
                input.getKey(),
                input.getValue());
    }
}