package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
public class MusicFileHandler extends FileHandler {

    public Music upload(CreateMusicRequestDTO dto) throws IOException {
        log.info("Music Upload : " + dto.musicName());

        /* music upload */
        upload(dto.music());
        String musicOriginalName = getOriginalName();
        String musicSavedName = getSavedName();
        String musicUrl = getFileUrl();

        /* image upload */
        upload(dto.image());
        String imageUrl = getFileUrl();

        return Music.create(
                dto.musicName(),
                dto.genres(),
                dto.lyrics(),
                musicOriginalName,
                musicSavedName,
                musicUrl,
                imageUrl
        );
    }

    @Override
    public Resource download(String musicUrl) throws IOException {
        log.info("Music Download from " + musicUrl);
        Path path = getPath(musicUrl);
        InputStream inputStream = Files.newInputStream(path);
        return new InputStreamResource(inputStream);
    }

    @Override
    public Resource display(String musicUrl) throws IOException {
        log.info("Music Display from " + musicUrl);
        Path path = getPath(musicUrl);
        InputStream inputStream = Files.newInputStream(path);
        return new InputStreamResource(inputStream);
    }
}
