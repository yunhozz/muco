package com.muco.musicservice.global.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicChartQueryDTO {

    private Long musicId;
    private Long musicianId;
    private String musicName;
    private String musicianName;
    private int ranking;
    private int likeCount;
    private String imageUrl;

    @QueryProjection
    public MusicChartQueryDTO(Long musicId, Long musicianId, String musicName, String musicianName, int ranking, int likeCount, String imageUrl) {
        this.musicId = musicId;
        this.musicianId = musicianId;
        this.musicName = musicName;
        this.musicianName = musicianName;
        this.ranking = ranking;
        this.likeCount = likeCount;
        this.imageUrl = imageUrl;
    }
}