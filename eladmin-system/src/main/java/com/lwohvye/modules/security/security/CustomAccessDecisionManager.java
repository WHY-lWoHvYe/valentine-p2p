package com.lwohvye.modules.security.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Hongyan Wang
 * @description
 * @date 2021/11/27 3:46 下午
 */
public class CustomAccessDecisionManager implements AccessDecisionManager {
    /**
     * 判断登录的用户是否具有请求Url所需要的角色信息，如果没有，就抛出AccessDeniedException异常
     *
     * @param auth   包含当前登录用户的信息
     * @param object 获取当前请求对象
     * @param ca     FilterInnovation...中getAttributes的返回值,即当前请求所需要的角色
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication auth, Object object, Collection<ConfigAttribute> ca) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute : ca) {
            if ("ROLE_LOGIN".equals(configAttribute.getAttribute()) && auth instanceof UsernamePasswordAuthenticationToken) { //如果请求Url需要的角色是ROLE_LOGIN，说明当前的Url用户登录后即可访问
                return;
            }
            var auths = auth.getAuthorities(); //获取登录用户具有的角色
            // var auths = SecurityUtils.getCurrentUser().getAuthorities();
            for (GrantedAuthority grantedAuthority : auths) {
                // TODO: 2021/11/27 对admin放行
                if (configAttribute.getAttribute().equals(grantedAuthority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}

