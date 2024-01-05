package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicSimpleQueryDTO {

    private Long id;
    private String musicName;
    private String musicianName;
    private int likeCount;

    @QueryProjection
    public MusicSimpleQueryDTO(Long id, String musicName, String musicianName, int likeCount) {
        this.id = id;
        this.musicName = musicName;
        this.musicianName = musicianName;
        this.likeCount = likeCount;
    }
}