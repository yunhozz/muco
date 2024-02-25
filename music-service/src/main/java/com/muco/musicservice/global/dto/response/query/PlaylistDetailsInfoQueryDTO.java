package com.muco.musicservice.global.dto.response.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PlaylistDetailsInfoQueryDTO {

    private String playlistName;
    private int musicCount;
    private List<PlaylistMusicInfoQueryDTO> musicInfoList;

    @QueryProjection
    public PlaylistDetailsInfoQueryDTO(String playlistName, int musicCount) {
        this.playlistName = playlistName;
        this.musicCount = musicCount;
    }

    public void updatePlaylistMusicInfoQueryDTOList(List<PlaylistMusicInfoQueryDTO> playlistMusicInfoQueryDTOList) {
        this.musicInfoList = playlistMusicInfoQueryDTOList;
    }
}