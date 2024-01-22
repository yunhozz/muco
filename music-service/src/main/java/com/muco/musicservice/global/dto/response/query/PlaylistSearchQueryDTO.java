package com.muco.musicservice.global.dto.response.query;

import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.querydsl.core.annotations.QueryProjection;

public record PlaylistSearchQueryDTO(
        Long id,
        String name,
        int likeCount
) implements SearchResponseDTO {
    @QueryProjection
    public PlaylistSearchQueryDTO {}
}