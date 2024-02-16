package com.muco.musicservice.domain.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Music music;

    private String name;

    private int musicCount;

    private int likeCount;

    public Playlist(Long userId, Music music, String name) {
        this.userId = userId;
        this.music = music;
        this.name = name;
    }

    public void update(String name) {
        this.name = name;
    }

    public void addMusicCount(int count) {
        musicCount += count;
    }

    public void subtractMusicCount(int count) {
        if (musicCount <= 0) {
            throw new IllegalArgumentException("음원 개수가 0 미만일 수 없습니다.");
        }
        musicCount -= count;
    }
}