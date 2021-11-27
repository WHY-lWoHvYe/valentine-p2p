package com.lwohvye.modules.system.handler;

import com.lwohvye.modules.system.annotation.UserTypeHandlerAnno;
import com.lwohvye.modules.system.enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hongyan Wang
 * @date 2021年11月02日 19:25
 */
@Slf4j
@Component
@UserTypeHandlerAnno(UserTypeEnum.DEV)
public final class DevUserTypeHandler implements AUserTypeHandler {
    @Override
    public List<GrantedAuthority> handler(Long userId) {
        log.warn(" salted fish：reverse。");
        Set<String> permissions = new HashSet<>();
        // 这里只是随便写一下，正常是走不到这个handler的
        permissions.add("ROLE_DEV");
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
