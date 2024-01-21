package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record MusicSearchQueryDTO(
        Long musicId,
        Long musicianId,
        String musicName,
        String musicianName,
        String musicImage,
        int playCount,
        int likeCount,
        LocalDateTime createdAt
) {
    @QueryProjection
    public MusicSearchQueryDTO {}
}