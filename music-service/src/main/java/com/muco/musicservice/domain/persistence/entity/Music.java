package com.muco.musicservice.domain.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Where(clause = "deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(joinColumns = @JoinColumn(name = "music_id"), name = "music_genre")
    private Set<Genre> genres = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String lyrics;

    private int playCount;

    private int likeCount;

    @ColumnDefault("-1")
    private int ranking;

    private String originalName;

    private String savedName;

    private String musicUrl;

    private String imageUrl;

    @ColumnDefault("null")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Music(String name, Set<Genre> genres, String lyrics, String originalName, String savedName, String musicUrl, String imageUrl) {
        this.name = name;
        this.genres = genres;
        this.lyrics = lyrics;
        this.originalName = originalName;
        this.savedName = savedName;
        this.musicUrl = musicUrl;
        this.imageUrl = imageUrl;
    }

    public static Music create(String name, Set<String> genreStrList, String lyrics, String originalName, String savedName, String musicUrl, String imageUrl) {
        Set<Genre> genres = genreStrList.stream()
                .map(Genre::of)
                .collect(Collectors.toSet());

        return Music.builder()
                .name(name)
                .genres(genres)
                .lyrics(lyrics)
                .originalName(originalName)
                .savedName(savedName)
                .musicUrl(musicUrl)
                .imageUrl(imageUrl)
                .build();
    }
}