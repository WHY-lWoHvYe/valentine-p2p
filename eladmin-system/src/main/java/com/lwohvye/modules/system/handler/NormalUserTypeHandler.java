package com.lwohvye.modules.system.handler;

import com.lwohvye.modules.system.annotation.UserTypeHandlerAnno;
import com.lwohvye.modules.system.domain.Role;
import com.lwohvye.modules.system.enums.UserTypeEnum;
import com.lwohvye.modules.system.repository.RoleRepository;
import com.lwohvye.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Hongyan Wang
 * @date 2021年11月02日 19:24
 */
@Slf4j
@Component
// 不能用下面这个注解，因为这个的使用方式，决定了要使用空参构造初始化。对于需要注入的对象，需特殊处理
//@RequiredArgsConstructor
@UserTypeHandlerAnno(UserTypeEnum.NORMAL)
public final class NormalUserTypeHandler implements AUserTypeHandler {

    private RoleRepository roleRepository;

    @Override
    public void doInit() {
        this.roleRepository = SpringContextHolder.getBean(RoleRepository.class);
    }

    @Override
    public List<GrantedAuthority> handler(Long userId) {
        log.warn(" banana：自由的气息，蕉迟但到。");
        Set<Role> roles = roleRepository.findByUserId(userId);
        var permissions = roles.stream().map(role -> "ROLE_" + role.getCode()).collect(Collectors.toSet());
        // .flatMap(role -> role.getResources().stream())
        // .map(Resource::getPattern)
        // .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
