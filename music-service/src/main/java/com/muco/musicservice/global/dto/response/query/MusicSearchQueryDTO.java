package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MusicSearchQueryDTO {

    private Long musicId;
    private Long musicianId;
    private String musicName;
    private String musicianName;
    private String musicImage;
    private int playCount;
    private int likeCount;
    private LocalDateTime createdAt;

    @QueryProjection
    public MusicSearchQueryDTO(Long musicId, Long musicianId, String musicName, String musicianName, String musicImage, int playCount, int likeCount, LocalDateTime createdAt) {
        this.musicId = musicId;
        this.musicianId = musicianId;
        this.musicName = musicName;
        this.musicianName = musicianName;
        this.musicImage = musicImage;
        this.playCount = playCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}