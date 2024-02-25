package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.application.exception.MusicNotFoundException;
import com.muco.musicservice.domain.application.exception.MusicPlaylistNotFoundException;
import com.muco.musicservice.domain.application.exception.PlaylistNotFoundException;
import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicPlaylist;
import com.muco.musicservice.domain.persistence.entity.Playlist;
import com.muco.musicservice.domain.persistence.repository.MusicPlaylistRepository;
import com.muco.musicservice.domain.persistence.repository.MusicRepository;
import com.muco.musicservice.domain.persistence.repository.PlaylistRepository;
import com.muco.musicservice.global.dto.request.CreatePlaylistRequestDTO;
import com.muco.musicservice.global.dto.request.UpdatePlaylistRequestDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistDetailsInfoQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicPlaylistRepository musicPlaylistRepository;

    @Transactional
    public Long createPlaylist(String userId, CreatePlaylistRequestDTO dto) {
        Music music = findMusicById(Long.valueOf(dto.musicId()));
        Playlist playlist = new Playlist(Long.valueOf(userId), music, dto.name());
        MusicPlaylist musicPlaylist = new MusicPlaylist(music, playlist);

        playlistRepository.save(playlist);
        musicPlaylistRepository.save(musicPlaylist);
        playlist.addMusicCount(1);

        return playlist.getId();
    }

    @Transactional
    public void addMusicOnPlaylist(String playlistId, String... musicIds) {
        List<Long> musicLongIds = Arrays.stream(musicIds)
                .map(Long::valueOf)
                .toList();
        List<Long> originalMusicIds = musicPlaylistRepository.findMusicIdsWherePlaylistId(Long.valueOf(playlistId));
        musicLongIds = createUniqueList(musicLongIds, originalMusicIds); // remove duplicate music ids

        List<Music> musicList = musicRepository.findAllById(musicLongIds);
        if (musicList.size() != musicLongIds.size()) {
            throw new MusicNotFoundException("추가하려는 음원이 존재하지 않습니다.");
        }

        Playlist playlist = findPlaylistById(Long.valueOf(playlistId));
        List<MusicPlaylist> musicPlaylists = new ArrayList<>() {{
            for (Music music : musicList) {
                MusicPlaylist musicPlaylist = new MusicPlaylist(music, playlist);
                add(musicPlaylist);
            }
        }};
        musicPlaylistRepository.saveAll(musicPlaylists);
        playlist.addMusicCount(musicLongIds.size());
    }

    @Transactional(readOnly = true)
    public PlaylistDetailsInfoQueryDTO findMusicListInPlaylist(String playlistId) {
        return musicPlaylistRepository.findDetailsOfPlaylistWherePlaylistId(Long.valueOf(playlistId));
    }

    @Transactional
    public void updatePlaylist(String playlistId, UpdatePlaylistRequestDTO dto) {
        Playlist playlist = findPlaylistById(Long.valueOf(playlistId));
        playlist.update(dto.name());
    }

    @Transactional
    public void deleteMusicFromPlaylist(String playlistId, String... musicIds) {
        List<Long> musicLongIds = Arrays.stream(musicIds)
                .map(Long::valueOf)
                .toList();
        List<Music> musicList = musicRepository.findAllById(musicLongIds);
        Playlist playlist = findPlaylistById(Long.valueOf(playlistId));

        List<Long> musicPlaylistIds = new ArrayList<>() {{
            for (Music music : musicList) {
                MusicPlaylist musicPlaylist = musicPlaylistRepository.findByMusicAndPlaylist(music, playlist)
                        .orElseThrow(() -> new MusicPlaylistNotFoundException(
                                String.format("해당 music_playlist 를 찾을 수 없습니다. music id = %s, playlist id = %s",
                                        music.getId(), playlist.getId())
                        ));
                add(musicPlaylist.getId());
            }
        }};

        musicPlaylistRepository.deleteAllById(musicPlaylistIds);
        playlist.subtractMusicCount(musicLongIds.size());
    }

    @Transactional
    public void deletePlaylist(String playlistId) {
        Playlist playlist = findPlaylistById(Long.valueOf(playlistId));
        List<Long> musicPlaylistIds = musicPlaylistRepository.findIdsWherePlaylistId(playlist.getId());
        musicPlaylistRepository.deleteAllById(musicPlaylistIds);
        playlistRepository.delete(playlist);
    }

    private Music findMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new MusicNotFoundException("해당 음원을 찾을 수 없습니다. id = " + id));
    }

    private Playlist findPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("해당 플레이리스트를 찾을 수 없습니다. id = " + id));
    }

    private static <T> List<T> createUniqueList(List<T> list1, List<T> list2) {
        List<T> uniqueList = new ArrayList<>(list1);
        uniqueList.removeAll(list2);
        return uniqueList;
    }
}
