package com.muco.musicservice.domain.persistence.repository;

import com.muco.musicservice.global.dto.response.query.MusicianNameQueryDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistDetailsInfoQueryDTO;
import com.muco.musicservice.global.dto.response.query.PlaylistMusicInfoQueryDTO;
import com.muco.musicservice.global.dto.response.query.QMusicianNameQueryDTO;
import com.muco.musicservice.global.dto.response.query.QPlaylistDetailsInfoQueryDTO;
import com.muco.musicservice.global.dto.response.query.QPlaylistMusicInfoQueryDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.muco.musicservice.domain.persistence.entity.QMusic.music;
import static com.muco.musicservice.domain.persistence.entity.QMusicMusician.musicMusician;
import static com.muco.musicservice.domain.persistence.entity.QMusicPlaylist.musicPlaylist;
import static com.muco.musicservice.domain.persistence.entity.QMusician.musician;
import static com.muco.musicservice.domain.persistence.entity.QPlaylist.playlist;

@Repository
@RequiredArgsConstructor
public class MusicPlaylistCustomRepositoryImpl implements MusicPlaylistCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PlaylistDetailsInfoQueryDTO findDetailsOfPlaylistWherePlaylistId(Long playlistId) {
        List<PlaylistMusicInfoQueryDTO> playlistMusicInfoList = queryFactory
                .select(new QPlaylistMusicInfoQueryDTO(
                        music.id,
                        music.musicUrl,
                        music.imageUrl,
                        music.name
                ))
                .from(musicPlaylist)
                .join(musicPlaylist.music, music)
                .join(musicPlaylist.playlist, playlist)
                .where(playlist.id.eq(playlistId))
                .fetch();

        List<Long> musicIds = playlistMusicInfoList.stream()
                .map(PlaylistMusicInfoQueryDTO::getMusicId)
                .toList();

        List<MusicianNameQueryDTO> musicianNameList = queryFactory
                .select(new QMusicianNameQueryDTO(
                        music.id,
                        musician.nickname
                ))
                .from(musicMusician)
                .join(musicMusician.music, music)
                .join(musicMusician.musician, musician)
                .where(music.id.in(musicIds))
                .fetch();

        Map<Long, List<MusicianNameQueryDTO>> musicianNameListMap = musicianNameList.stream()
                .collect(Collectors.groupingBy(MusicianNameQueryDTO::musicId));

        playlistMusicInfoList.forEach(playlistMusicInfoQueryDTO -> {
            Long musicId = playlistMusicInfoQueryDTO.getMusicId();
            List<MusicianNameQueryDTO> musicianNameDTOs = musicianNameListMap.get(musicId);
            String musicianNameStr = musicianNameDTOs.stream()
                    .map(MusicianNameQueryDTO::musicianName)
                    .collect(Collectors.joining(", "));
            playlistMusicInfoQueryDTO.updateMusicianName(musicianNameStr);
        });

        PlaylistDetailsInfoQueryDTO playlistDetailsInfo = queryFactory
                .select(new QPlaylistDetailsInfoQueryDTO(
                        playlist.name,
                        music.count().intValue()
                ))
                .from(musicPlaylist)
                .join(musicPlaylist.playlist, playlist)
                .join(musicPlaylist.music, music)
                .where(playlist.id.eq(playlistId))
                .fetchOne();

        playlistDetailsInfo.updatePlaylistMusicInfoQueryDTOList(playlistMusicInfoList);

        return playlistDetailsInfo;
    }
}
