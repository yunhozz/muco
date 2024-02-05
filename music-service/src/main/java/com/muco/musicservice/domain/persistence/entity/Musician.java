package com.muco.musicservice.domain.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Musician extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String email;

    private int age;

    private String nickname;

    @ColumnDefault("0")
    private int likeCount;

    @ColumnDefault("0")
    private int musicCount;

    private String imageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private Musician(Long userId, String email, int age, String nickname, String imageUrl) {
        this.userId = userId;
        this.email = email;
        this.age = age;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }

    public static Musician create(Long userId, String email, int age, String nickname, String imageUrl) {
        return Musician.builder()
                .userId(userId)
                .email(email)
                .age(age)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .build();
    }

    public void addMusicCount(int count) {
        musicCount += count;
    }
}