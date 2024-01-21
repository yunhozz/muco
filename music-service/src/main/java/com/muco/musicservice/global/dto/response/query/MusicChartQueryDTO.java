package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;

public record MusicChartQueryDTO(
        Long musicId,
        Long musicianId,
        String musicName,
        String musicianName,
        int ranking,
        int likeCount,
        String imageUrl
) {
    @QueryProjection
    public MusicChartQueryDTO {}
}