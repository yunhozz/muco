package com.muco.musicservice.global.dto.response.query;

import com.muco.musicservice.global.dto.response.SearchResponseDTO;
import com.querydsl.core.annotations.QueryProjection;

public record MusicSimpleQueryDTO(
        Long id,
        String musicName,
        String musicianName,
        int likeCount
) implements SearchResponseDTO {
    @QueryProjection
    public MusicSimpleQueryDTO {}
}