/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lwohvye

import com.lwohvye.core.annotation.rest.AnonymousGetMapping
import com.lwohvye.core.utils.SpringContextHolder
import com.lwohvye.sys.common.annotation.ApiVersion
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

/**
 * Portal
 *
 * @author lWoHvYe
 * @date 2022/08/05 00:00:00
 */
@EnableAsync // 开启异步
@RestController
@Hidden
@SpringBootApplication // 核心配置类
// 通过exclude SecurityAutoConfiguration可以屏蔽Spring Security的相关Control，这比定一个Config然后anyRequest().permitAll()要优雅，直接exclude AutoConfig，这是之前没有想到的方法，后续在别的地方应该还会用到
//@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
@EnableTransactionManagement // 开启事务
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // 开启Jpa审计
@EnableRetry //开启重试机制
@EnableConfigurationProperties //开启 @ConfigurationProperties 注解
// 以下注解在其他类上有配置
// @EnableCaching
// @EnableOpenApi
// @EnableWebMvc
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
open class ValentineP2P {

    companion object {
        /**
         * Spring Boot入口
         *
         * @param args /
         * @date 2021/11/23 9:43 上午
         */
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ValentineP2P::class.java, *args)
        }
    }

    /**
     * 注入Bean对象。用于从ApplicationContext中获取bean实例等
     *
     * @date 2021/11/23 9:42 上午
     */
    @Bean
    open fun springContextHolder(): SpringContextHolder {
        return SpringContextHolder()
    }

    /**
     * 访问首页提示
     * 因为自定义了处理逻辑，所以下面这俩的RequestCondition是不一样的，所以能共存，且因为定义了优先新版本，所以在v1时走第一个，[v2+ 走第二个
     * 但注意⚠️：这种path上带param是与定义的匿名访问注解AnonymousAccess不兼容的（不生效）
     * @see com.lwohvye.sys.common.handler.ApiVersionRequestMappingHandlerMapping
     * @return /
     */
    @ApiVersion
    @AnonymousGetMapping("/valentine/{version}/p2p")
    fun index(@PathVariable version: String): String {
        return "Backend service started successfully"
    }

    @ApiVersion(2) // 指定从v2开始
    @AnonymousGetMapping("/valentine/{version}/p2p", "/valentine/{version}/default") // @RequestMapping的path是支持多个的
    fun indexVersion(@PathVariable version: String): String {
        return String.format("Version %s Backend service started successfully", version)
    }

    /**
     * 匹配采用的最佳适配，当传v4时，会匹配到这个方法
     */
    @AnonymousGetMapping("/valentine/v4/p2p")
    fun indexClVer(): String {
        return String.format("ClVersion %s Backend service started successfully", "v4")
    }
}
