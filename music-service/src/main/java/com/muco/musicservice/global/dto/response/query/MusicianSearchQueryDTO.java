package com.muco.musicservice.global.dto.response.query;

import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.querydsl.core.annotations.QueryProjection;

public record MusicianSearchQueryDTO(
        Long id,
        String name,
        int likeCount,
        String imageUrl
) implements SearchResponseDTO {
    @QueryProjection
    public MusicianSearchQueryDTO {}
}