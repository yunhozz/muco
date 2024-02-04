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
public class MusicFileHandler {

    private final FileHandler fileHandler;

    public MusicFileHandler() {
        fileHandler = new FileHandler() {
            @Override
            protected Resource download(String musicUrl) throws IOException {
                Path path = getPath(musicUrl);
                InputStream inputStream = Files.newInputStream(path);
                return new InputStreamResource(inputStream);
            }

            //TODO
            @Override
            protected Resource display(String musicUrl) throws IOException {
                return null;
            }
        };
    }

    public Music musicUpload(CreateMusicRequestDTO dto) throws IOException {
        log.info("Music Upload : " + dto.musicName());

        /* music upload */
        fileHandler.upload(dto.music());
        String musicOriginalName = fileHandler.getOriginalName();
        String musicSavedName = fileHandler.getSavedName();
        String musicUrl = fileHandler.getFileUrl();

        /* image upload */
        fileHandler.upload(dto.image());
        String imageUrl = fileHandler.getFileUrl();

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

    public Resource musicDownload(String musicUrl) throws IOException {
        log.info("Music Download from " + musicUrl);
        return fileHandler.download(musicUrl);
    }

    public String createMusicContentType(String musicUrl) throws IOException {
        log.info("Content Type : " + fileHandler.createContentType(musicUrl));
        return fileHandler.createContentType(musicUrl);
    }
}
