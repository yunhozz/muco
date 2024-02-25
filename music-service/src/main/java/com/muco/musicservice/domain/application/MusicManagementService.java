package com.muco.musicservice.domain.application;

import com.muco.musicservice.domain.application.exception.DownloadFailException;
import com.muco.musicservice.domain.application.exception.MusicNotFoundException;
import com.muco.musicservice.domain.application.exception.UploadFailException;
import com.muco.musicservice.domain.application.handler.ImageFileHandler;
import com.muco.musicservice.domain.application.handler.MusicFileHandler;
import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.domain.persistence.entity.MusicMusician;
import com.muco.musicservice.domain.persistence.entity.Musician;
import com.muco.musicservice.domain.persistence.repository.MusicMusicianRepository;
import com.muco.musicservice.domain.persistence.repository.MusicRepository;
import com.muco.musicservice.domain.persistence.repository.MusicianRepository;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import com.muco.musicservice.global.dto.request.UserInfoRequestDTO;
import com.muco.musicservice.global.dto.response.FileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicManagementService {

    private final MusicRepository musicRepository;
    private final MusicianRepository musicianRepository;
    private final MusicMusicianRepository musicMusicianRepository;

    private final MusicFileHandler musicHandler;
    private final ImageFileHandler imageHandler;

    @Transactional
    public Long registerMusic(CreateMusicRequestDTO createMusicRequestDTO, List<UserInfoRequestDTO> userInfoRequestDTOs) {
        Music music;
        try {
            music = musicHandler.upload(createMusicRequestDTO);
            musicRepository.save(music);

        } catch (IOException e) {
            throw new UploadFailException("파일 업로드에 실패하였습니다. 원인 : " + e.getLocalizedMessage());
        }

        for (UserInfoRequestDTO userInfoRequestDTO : userInfoRequestDTOs) {
            Long userId = Long.valueOf(userInfoRequestDTO.userId());
            Musician musician = musicianRepository.getMusicianWhenExistsByUserId(userId);
            if (musician == null) {
                musician = Musician.create(
                        userId,
                        userInfoRequestDTO.email(),
                        userInfoRequestDTO.age(),
                        userInfoRequestDTO.nickname(),
                        userInfoRequestDTO.userImageUrl()
                );
                musicianRepository.save(musician);
            }
            musician.addMusicCount(1);
            MusicMusician musicMusician = new MusicMusician(music, musician);
            musicMusicianRepository.save(musicMusician);
        }
        return music.getId();
    }

    @Transactional(readOnly = true)
    public FileResponseDTO downloadMusic(Long musicId) {
        Music music = findMusicById(musicId);
        String musicUrl = music.getMusicUrl();

        try {
            Resource resource = musicHandler.download(musicUrl);
            String contentType = musicHandler.createContentType(musicUrl);
            return new FileResponseDTO(resource, contentType, music.getOriginalName());

        } catch (IOException e) {
            throw new DownloadFailException("파일 다운로드에 실패하였습니다. 원인 : " + e.getLocalizedMessage());
        }
    }

    @Transactional(readOnly = true)
    public FileResponseDTO displayMusicImage(Long musicId) {
        Music music = findMusicById(musicId);
        String imageUrl = music.getImageUrl();

        try {
            Resource resource = imageHandler.display(imageUrl);
            String contentType = imageHandler.createContentType(imageUrl);
            return new FileResponseDTO(resource, contentType, music.getOriginalName());

        } catch (IOException e) {
            throw new DownloadFailException("파일 다운로드에 실패하였습니다. 원인 : " + e.getLocalizedMessage());
        }
    }

    private Music findMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new MusicNotFoundException("해당 음원이 존재하지 않습니다. id : " + id));
    }
}