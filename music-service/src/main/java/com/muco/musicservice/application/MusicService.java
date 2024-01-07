package com.muco.musicservice.application;

import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.response.MusicListResponseDTO;
import com.muco.musicservice.global.dto.response.query.MusicChartQueryDTO;
import com.muco.musicservice.global.dto.response.query.MusicSimpleQueryDTO;
import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.persistence.entity.Music;
import com.muco.musicservice.persistence.entity.MusicMusician;
import com.muco.musicservice.persistence.entity.Musician;
import com.muco.musicservice.persistence.repository.MusicMusicianRepository;
import com.muco.musicservice.persistence.repository.MusicRepository;
import com.muco.musicservice.persistence.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO dto) {
        Musician musician = Musician.create(dto.getEmail(), dto.getAge(), dto.getNickname(), dto.getUserImageUrl());
        Music music = Music.create(dto.getMusicName(), dto.getGenres(), dto.getLyrics(), dto.getMusicImageUrl());
        MusicMusician musicMusician = new MusicMusician(music, musician);

        musicRepository.save(music);
        musicianRepository.save(musician);
        musicMusicianRepository.save(musicMusician);

        return music.getId();
    }

    @Transactional(readOnly = true)
    public Slice<MusicChartQueryDTO> getMusicChartList(Integer cursorRank, Pageable pageable) {
        return musicMusicianRepository.getMusicChartList(cursorRank, pageable);
    }

    @Transactional(readOnly = true)
    public List<MusicListResponseDTO> getMusicListByKeywordSearchOnTotalCategory(String keyword) {
        return new ArrayList<>() {{
            for (SearchCategory category : SearchCategory.values()) {
                List<MusicSimpleQueryDTO> musicList = musicMusicianRepository.getMusicSimpleListByKeywordAndCategory(keyword, category);
                add(new MusicListResponseDTO(category, musicList.size(), musicList));
            }
        }};
    }
}