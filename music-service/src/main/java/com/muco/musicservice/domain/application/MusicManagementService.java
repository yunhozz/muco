package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicMusician;
import com.muco.musicservice.domain.persistence.entity.Musician;
import com.muco.musicservice.domain.persistence.repository.MusicMusicianRepository;
import com.muco.musicservice.domain.persistence.repository.MusicRepository;
import com.muco.musicservice.domain.persistence.repository.MusicianRepository;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicManagementService {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO dto) {
        Musician musician = Musician.create(dto.email(), dto.age(), dto.nickname(), dto.userImageUrl());
        Music music = Music.create(dto.musicName(), dto.genres(), dto.lyrics(), dto.musicImageUrl());
        MusicMusician musicMusician = new MusicMusician(music, musician);

        musicRepository.save(music);
        musicianRepository.save(musician);
        musicMusicianRepository.save(musicMusician);

        return music.getId();
    }

    @Transactional
    public Long createPlaylist(Long userId) {
        return null;
    }

    @Transactional
    public Long addMusicOnPlaylist(Long musicId) {
        return null;
    }

    @Transactional
    public void deleteMusic(Long musicId, Long userId) {
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 음원이 존재하지 않습니다. id = " + musicId));
    }

    @Transactional
    public void deletePlaylist(Long musicListId, Long userId) {

    }
}