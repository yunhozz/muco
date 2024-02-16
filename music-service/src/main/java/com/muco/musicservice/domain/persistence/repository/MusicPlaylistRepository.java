package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicPlaylist;
import com.muco.musicservice.domain.persistence.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MusicPlaylistRepository extends JpaRepository<MusicPlaylist, Long> {

    Optional<MusicPlaylist> findByMusicAndPlaylist(Music music, Playlist playlist);

    @Query("select mp.id from MusicPlaylist mp join mp.playlist p where p.id = :playlistId")
    List<Long> findWherePlaylistId(@Param("playlistId") Long playlistId);

    default void deleteWhereMusicAndPlaylist(Music music, Playlist playlist) {
        MusicPlaylist musicPlaylist = findByMusicAndPlaylist(music, playlist)
                .orElseThrow();
        delete(musicPlaylist);
    }
}
