package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public final class MusicFileHandler extends FileHandler<Music, CreateMusicRequestDTO> {

    private MusicFileHandler() {}

    @Override
    public Music upload(CreateMusicRequestDTO dto) throws IOException {
        super.transferFile(dto.file());
        return Music.create(
                dto.musicName(),
                dto.genres(),
                dto.lyrics(),
                originalName,
                savedName,
                fileUrl,
                null
        );
    }

    @Override
    public Resource download(String fileName) throws IOException {
        Path path = getPath(fileName);
        InputStream inputStream = Files.newInputStream(path);
        return new InputStreamResource(inputStream);
    }

    //TODO
    @Override
    public Resource display(String fileName) throws IOException {
        return null;
    }

    @Override
    public String createContentType(String fileName) throws IOException {
        Path path = getPath(fileName);
        return Files.probeContentType(path);
    }

    @Override
    protected Path getPath(String name) {
        return Paths.get(fileUrl + "/" + name);
    }
}
