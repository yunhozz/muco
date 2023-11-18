package com.muco.authservice.persistence.entity;

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

    public static UserProfile createUserProfile(User user, String email, String name, int age, String nickname, String imageUrl) {
        UserProfile userProfile = new UserProfile();
        userProfile.user = user;
        userProfile.email = email;
        userProfile.name = name;
        userProfile.age = age;
        userProfile.nickname = nickname;
        userProfile.imageUrl = imageUrl;

        return userProfile;
    }

    public void updateBySocialLogin(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}