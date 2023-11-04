package com.muco.authservice.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ROLE_ADMIN","운영자"),
    USER("ROLE_USER", "일반 사용자"),
    GUEST("ROLE_GUEST", "게스트")

    ;

    private final String auth;

    @Getter
    private final String info;

    @Override
    public String getAuthority() {
        return auth;
    }
}