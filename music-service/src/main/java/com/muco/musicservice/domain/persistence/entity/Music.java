package com.muco.musicservice.domain.persistence.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Where(clause = "deleted_at is null")
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

    @ColumnDefault("0")
    private int playCount;

    @ColumnDefault("0")
    private int likeCount;

    @ColumnDefault("-1")
    private int ranking;

    private String originalName;

    private String savedName;

    private String fileUrl;

    private String imageUrl;

    @ColumnDefault("null")
    private LocalDateTime deletedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Music(String name, List<Genre> genres, String lyrics, String originalName, String savedName, String fileUrl, String imageUrl) {
        this.name = name;
        this.genres = genres;
        this.lyrics = lyrics;
        this.originalName = originalName;
        this.savedName = savedName;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
    }

    public static Music create(String name, List<Genre> genres, String lyrics, String originalName, String savedName, String fileUrl, String imageUrl) {
        return Music.builder()
                .name(name)
                .genres(genres)
                .lyrics(lyrics)
                .originalName(originalName)
                .savedName(savedName)
                .fileUrl(fileUrl)
                .imageUrl(imageUrl)
                .build();
    }
}