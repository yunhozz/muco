package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaylistMusicInfoQueryDTO {

    private Long musicId;
    private String musicUrl;
    private String musicImgUrl;
    private String musicName;
    private String musicianName;

    @QueryProjection
    public PlaylistMusicInfoQueryDTO(Long musicId, String musicUrl, String musicImgUrl, String musicName) {
        this.musicId = musicId;
        this.musicUrl = musicUrl;
        this.musicImgUrl = musicImgUrl;
        this.musicName = musicName;
    }

    public void updateMusicianName(String musicianName) {
        this.musicianName = musicianName;
    }
}