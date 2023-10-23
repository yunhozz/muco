package com.muco.authservice.global.auth.security;

import com.muco.authservice.global.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Long userId;

    @Getter
    private final Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            for (Role role : roles) {
                add(new SimpleGrantedAuthority(role.getAuthority()));
            }
        }};
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