package com.muco.musicservice.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "genre", joinColumns = @JoinColumn(name = "music_id"))
    private List<Genre> genres = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String lyrics;

    private int playCount;

    private int likeCount;

    private int ranking;

    private String imageUrl;

    private Music(String name, List<Genre> genres, String lyrics, int playCount, int likeCount, int ranking, String imageUrl) {
        this.name = name;
        this.genres = genres;
        this.lyrics = lyrics;
        this.playCount = playCount;
        this.likeCount = likeCount;
        this.ranking = ranking;
        this.imageUrl = imageUrl;
    }

    public static Music create(String name, List<Genre> genres, String lyrics, String imageUrl) {
        return new Music(name, genres, lyrics, 0, 0, -1, imageUrl);
    }
}