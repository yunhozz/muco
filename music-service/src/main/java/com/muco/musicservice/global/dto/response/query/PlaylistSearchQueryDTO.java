package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;

public record PlaylistSearchQueryDTO(
        Long id,
        String name,
        int likeCount
) {
    @QueryProjection
    public PlaylistSearchQueryDTO {}
}