package com.muco.authservice.persistence.entity;

import com.muco.authservice.global.enums.LoginType;
import com.muco.authservice.global.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginType loginType; // GOOGLE, KAKAO, NAVER, LOCAL

    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>(); // ADMIN, USER, GUEST

    public User(LoginType loginType) {
        this.loginType = loginType;
        roles.add(Role.GUEST);
    }

    public void updateBySocialLogin(LoginType loginType) {
        this.loginType = loginType;
        roles.add(Role.USER);
    }

    public void addUserByEmailVerify() {
        roles.add(Role.USER);
    }
}