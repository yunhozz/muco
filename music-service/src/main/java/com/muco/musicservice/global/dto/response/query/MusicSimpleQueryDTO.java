package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;

public record MusicSimpleQueryDTO(
        Long id,
        String musicName,
        String musicianName,
        int likeCount
) {
    @QueryProjection
    public MusicSimpleQueryDTO {}
}