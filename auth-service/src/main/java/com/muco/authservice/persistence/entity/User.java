package com.muco.authservice.persistence.entity;

import com.muco.authservice.global.enums.Role;
import com.muco.authservice.global.enums.SocialType;
import jakarta.persistence.Column;
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

    @Column(unique = true, length = 50, nullable = false)
    private String email;

    private String password;

    @Column(length = 10)
    private String name;

    private int age;

    @Column(length = 8, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // GOOGLE, KAKAO, NAVER

    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>(); // ADMIN, USER, GUEST
}