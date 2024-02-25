package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;

public record MusicianNameQueryDTO(
        Long musicId,
        String musicianName
) {
    @QueryProjection
    public MusicianNameQueryDTO {}
}
