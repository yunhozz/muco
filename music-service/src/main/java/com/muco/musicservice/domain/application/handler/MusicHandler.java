package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.Music;
import com.muco.musicservice.global.dto.request.CreateMusicRequestDTO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class MusicHandler implements FileHandler<Music, CreateMusicRequestDTO> {

    private String fileUrl;
    protected String contentType;

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
    public Resource download(String fileName) {
        Path path = Paths.get(fileUrl + "/" + fileName);
        try(InputStream inputStream = Files.newInputStream(path)) {
            contentType = Files.probeContentType(path);
            return new InputStreamResource(inputStream);

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }

    @Override
    public Resource display(String fileName) {
        Path path = Paths.get(fileUrl + "/" + fileName);
        try {
            Resource resource = new FileSystemResource(path);
            contentType = Files.probeContentType(path);
            return resource;

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
    }
}
