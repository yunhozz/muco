package com.muco.authservice.global.auth.security;

import com.muco.authservice.global.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserDetailsImpl implements UserDetails, OAuth2User {

    private final Long userId;

    @Getter
    private final Set<Role> roles;

    private final Map<String, Object> attributes;

    private UserDetailsImpl(Long userId, Set<Role> roles) {
        this.userId = userId;
        this.roles = roles;
        this.attributes = new ConcurrentHashMap<>();
    }

    private UserDetailsImpl(Long userId, Set<Role> roles, Map<String, Object> attributes) {
        this.userId = userId;
        this.roles = roles;
        this.attributes = attributes;
    }

    public static UserDetailsImpl ofLocal(Long userId, Set<Role> roles) {
        return new UserDetailsImpl(userId, roles);
    }

    public static UserDetailsImpl ofSocial(Long userId, Set<Role> roles, Map<String, Object> attributes) {
        return new UserDetailsImpl(userId, roles, attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            for (Role role : roles) {
                add(new SimpleGrantedAuthority(role.getAuthority()));
            }
        }};
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getUsername() {
        return userId.toString();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}