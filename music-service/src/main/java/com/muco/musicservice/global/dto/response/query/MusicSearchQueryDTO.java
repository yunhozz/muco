package com.muco.musicservice.global.dto.response.query;

import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record MusicSearchQueryDTO(
        Long musicId,
        Long musicianId,
        String musicName,
        String musicianName,
        int playCount,
        int likeCount,
        String musicUrl,
        String imageUrl,
        LocalDateTime createdAt
) implements SearchResponseDTO {
    @QueryProjection
    public MusicSearchQueryDTO {}
}