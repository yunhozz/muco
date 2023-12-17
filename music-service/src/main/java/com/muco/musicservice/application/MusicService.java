package com.muco.musicservice.application;

import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.persistence.entity.Music;
import com.muco.musicservice.persistence.entity.MusicMusician;
import com.muco.musicservice.persistence.entity.Musician;
import com.muco.musicservice.persistence.repository.MusicMusicianRepository;
import com.muco.musicservice.persistence.repository.MusicRepository;
import com.muco.musicservice.persistence.repository.MusicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO dto) {
        Musician musician = Musician.create(dto.getEmail(), dto.getAge(), dto.getNickname(), dto.getUserImageUrl());
        Music music = Music.create(dto.getMusicName(), dto.getGenres(), dto.getLyrics());
        MusicMusician musicMusician = new MusicMusician(music, musician);

        musicRepository.save(music);
        musicianRepository.save(musician);
        musicMusicianRepository.save(musicMusician);

        return music.getId();
    }
}