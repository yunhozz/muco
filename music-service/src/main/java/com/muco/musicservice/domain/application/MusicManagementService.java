package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.application.handler.MusicHandler;
import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicMusician;
import com.muco.musicservice.domain.persistence.entity.Musician;
import com.muco.musicservice.domain.persistence.repository.MusicMusicianRepository;
import com.muco.musicservice.domain.persistence.repository.MusicRepository;
import com.muco.musicservice.domain.persistence.repository.MusicianRepository;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.response.FileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MusicManagementService extends MusicHandler {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO dto, MultipartFile file) {
        Music music = upload(file, dto);
        Musician musician = Musician.create(dto.email(), dto.age(), dto.nickname(), dto.userImageUrl());
        MusicMusician musicMusician = new MusicMusician(music, musician);

        musicRepository.save(music);
        musicianRepository.save(musician);
        musicMusicianRepository.save(musicMusician);

        return music.getId();
    }

    @Transactional(readOnly = true)
    public FileResponseDTO downloadMusic(Long musicId) {
        Music music = findMusicById(musicId);
        String savedName = music.getSavedName();
        Resource resource = download(savedName);
        return new FileResponseDTO(resource, contentType, savedName);
    }

    private Music findMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 음원이 존재하지 않습니다. id : " + id));
    }
}