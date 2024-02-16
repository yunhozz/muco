package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicPlaylist;
import com.muco.musicservice.domain.persistence.entity.Playlist;
import com.muco.musicservice.domain.persistence.repository.MusicPlaylistRepository;
import com.muco.musicservice.domain.persistence.repository.MusicRepository;
import com.muco.musicservice.domain.persistence.repository.PlaylistRepository;
import com.muco.musicservice.global.dto.request.CreatePlaylistRequestDTO;
import com.muco.musicservice.global.dto.request.UpdatePlaylistRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicPlaylistRepository musicPlaylistRepository;

    @Transactional
    public void createPlaylist(Long userId, CreatePlaylistRequestDTO dto) {
        Music music = findMusicById(dto.musicId());
        Playlist playlist = new Playlist(userId, music, dto.name());
        MusicPlaylist musicPlaylist = new MusicPlaylist(music, playlist);

        playlistRepository.save(playlist);
        musicPlaylistRepository.save(musicPlaylist);
        playlist.addMusicCount(1);
    }

    @Transactional
    public void addMusicOnPlaylist(Long playlistId, Long... musicIds) {
        List<Music> musicList = musicRepository.findAllById(List.of(musicIds));
        Playlist playlist = findPlaylistById(playlistId);

        List<MusicPlaylist> musicPlaylists = new ArrayList<>() {{
            for (Music music : musicList) {
                MusicPlaylist musicPlaylist = new MusicPlaylist(music, playlist);
                add(musicPlaylist);
            }
        }};

        musicPlaylistRepository.saveAll(musicPlaylists);
        playlist.addMusicCount(musicIds.length);
    }

    @Transactional
    public void updatePlaylist(Long playlistId, UpdatePlaylistRequestDTO dto) {
        Playlist playlist = findPlaylistById(playlistId);
        playlist.update(dto.name());
    }

    @Transactional
    public void deleteMusicFromPlaylist(Long playlistId, Long... musicIds) {
        List<Music> musicList = musicRepository.findAllById(List.of(musicIds));
        Playlist playlist = findPlaylistById(playlistId);

        List<Long> musicPlaylistIds = new ArrayList<>() {{
            for (Music music : musicList) {
                MusicPlaylist musicPlaylist = musicPlaylistRepository.findById(music.getId())
                        .orElseThrow();
                add(musicPlaylist.getId());
            }
        }};

        musicPlaylistRepository.deleteAllById(musicPlaylistIds);
        playlist.subtractMusicCount(musicIds.length);
    }

    @Transactional
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = findPlaylistById(playlistId);
        List<Long> musicPlaylistIds = musicPlaylistRepository.findWherePlaylistId(playlist.getId());
        musicPlaylistRepository.deleteAllById(musicPlaylistIds);
        playlistRepository.delete(playlist);
    }

    private Music findMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow();
    }

    private Playlist findPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow();
    }
}
