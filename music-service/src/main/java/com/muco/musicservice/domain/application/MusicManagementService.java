package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.application.exception.DownloadFailException;
import com.muco.musicservice.domain.application.exception.MusicNotFoundException;
import com.muco.musicservice.domain.application.exception.UploadFailException;
import com.muco.musicservice.domain.application.handler.MusicFileHandler;
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

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MusicManagementService {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    private final MusicFileHandler fileHandler;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO dto) {
        try {
            Music music = fileHandler.musicUpload(dto);
            Musician musician = Musician.create(dto.email(), dto.age(), dto.nickname(), dto.userImageUrl());
            MusicMusician musicMusician = new MusicMusician(music, musician);

            musicRepository.save(music);
            musicianRepository.save(musician);
            musicMusicianRepository.save(musicMusician);

            return music.getId();

        } catch (IOException e) {
            throw new UploadFailException(e.getLocalizedMessage());
        }
    }

    @Transactional(readOnly = true)
    public FileResponseDTO downloadMusic(Long musicId) {
        Music music = findMusicById(musicId);
        String savedName = music.getSavedName();

        try {
            Resource resource = fileHandler.musicDownload(savedName);
            String contentType = fileHandler.createMusicContentType(savedName);
            return new FileResponseDTO(resource, contentType, savedName);

        } catch (IOException e) {
            throw new DownloadFailException(e.getLocalizedMessage());
        }
    }

    private Music findMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new MusicNotFoundException("해당 음원이 존재하지 않습니다. id : " + id));
    }
}