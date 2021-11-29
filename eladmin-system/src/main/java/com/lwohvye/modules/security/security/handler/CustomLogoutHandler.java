package com.lwohvye.modules.security.security.handler;

import com.lwohvye.modules.security.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @description 处理退出逻辑
 * @date 2021/11/27 9:42 上午
 */
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenProvider tokenProvider; // Token

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 根据过滤器的执行顺序。LogoutFilter在UsernamePasswordAuthenticationFilter之前执行。所以在这里时，是还没包办好的，要自己处理
        if (Objects.isNull(authentication)) {
            try {
                String token = tokenProvider.getToken(request);
                authentication = tokenProvider.getAuthentication(token);
            } catch (Exception ignored) {
                // 这里出异常后，不要再抛了，因为会被异常处理器处理，可能被重定向到logout，这就构成♻️了
            }
            // 如果还拿不到，就返回了。无情
            if (Objects.isNull(authentication))
                return;
            // 先放进去，因为后面还有个处理登出成功的要用
            // SecurityContextLogoutHandler在这之后执行，会清除信息
            // SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            log.info("username: {}  is offline now", username);
        }
    }
}