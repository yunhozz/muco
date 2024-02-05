package com.muco.authservice.domain.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(unique = true, length = 50)
    private String email;

    @Column(length = 10)
    private String name;

    private int age;

    @Column(unique = true, length = 8)
    private String nickname;

    private String imageUrl;

    private UserProfile(User user, String email, String name, int age, String nickname, String imageUrl) {
        this.user = user;
        this.email = email;
        this.name = name;
        this.age = age;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public static UserProfile createUserProfile(User user, String email, String name, int age, String nickname, String imageUrl) {
        return new UserProfile(user, email, name, age, nickname, imageUrl);
    }

    public void updateBySocialLogin(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}