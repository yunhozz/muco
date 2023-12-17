package com.muco.musicservice.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Musician extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private int age;

    private String nickname;

    private String imageUrl;

    private Musician(String email, int age, String nickname, String imageUrl) {
        this.email = email;
        this.age = age;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public static Musician create(String email, int age, String nickname, String imageUrl) {
        return new Musician(email, age, nickname, imageUrl);
    }
}