package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.global.dto.response.query.PlaylistDetailsInfoQueryDTO;

public interface MusicPlaylistCustomRepository {
    PlaylistDetailsInfoQueryDTO findDetailsOfPlaylistWherePlaylistId(Long playlistId);
}
