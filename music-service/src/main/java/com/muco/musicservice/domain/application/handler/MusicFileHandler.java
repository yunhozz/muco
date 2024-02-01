package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public final class MusicFileHandler {

    private FileHandler fileHandler;

    private MusicFileHandler() {}

    public MusicFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public Music upload(CreateMusicRequestDTO dto) throws IOException {
        fileHandler.transferFiles(new MultipartFile[] {dto.music(), dto.image()});
        return Music.create(
                dto.musicName(),
                dto.genres(),
                dto.lyrics(),
                fileHandler.getOriginalName(),
                fileHandler.getSavedName(),
                fileHandler.getFileUrls()[0],
                fileHandler.getFileUrls()[1]
        );
    }

    public Resource download(String fileName) throws IOException {
        Path path = getPath(fileName);
        InputStream inputStream = Files.newInputStream(path);
        return new InputStreamResource(inputStream);
    }

    //TODO
    public Resource display(String fileName) throws IOException {
        return null;
    }

    public String createContentType(String fileName) throws IOException {
        Path path = getPath(fileName);
        return Files.probeContentType(path);
    }

    private Path getPath(String fileName) {
        String fileUrl = fileHandler.getFileUrls()[0];
        return Paths.get(fileUrl + "/" + fileName);
    }
}
