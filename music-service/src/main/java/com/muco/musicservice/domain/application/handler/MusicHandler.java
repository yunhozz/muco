package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class MusicHandler implements FileHandler<Music, CreateMusicRequestDTO> {

    private String fileUrl = null;

    @Override
    public Music upload(MultipartFile file, CreateMusicRequestDTO dto) {
        fileUrl = createSavedPath();
        String originalName = file.getOriginalFilename();

        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        String savedName = uuid + extension;

        try {
            file.transferTo(new File(fileUrl + "/" + savedName));
            return Music.create(dto.musicName(), dto.genres(), dto.lyrics(), originalName, savedName, fileUrl, null);

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }

    @Override
    public Resource download(String fileName) throws IOException {
        Path path = getPath(fileName);
        InputStream inputStream = Files.newInputStream(path);
        return new InputStreamResource(inputStream);
    }

    @Override
    public Resource display(String fileName) {
        Path path = getPath(fileName);
        return new FileSystemResource(path);
    }

    public String createContentType(String fileName) throws IOException {
        Path path = getPath(fileName);
        return Files.probeContentType(path);
    }

    private Path getPath(String name) {
        return Paths.get(fileUrl + "/" + name);
    }
}
