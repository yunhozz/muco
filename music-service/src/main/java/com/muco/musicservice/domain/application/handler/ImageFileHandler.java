package com.muco.musicservice.domain.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
public class ImageFileHandler {

    private final FileHandler fileHandler;

    public ImageFileHandler() {
        fileHandler = new FileHandler() {
            @Override
            protected Resource download(String imageUrl) throws IOException {
                return null; //Can't download images
            }

            @Override
            protected Resource display(String imageUrl) throws IOException {
                Path path = getPath(imageUrl);
                byte[] fileArray;
                try (FileInputStream fis = new FileInputStream(path.toString());
                     ByteArrayOutputStream baos = new ByteArrayOutputStream())
                {
                    int readCount = 0;
                    byte[] buffer = new byte[1024];
                    while ((readCount = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, readCount);
                    }
                    fileArray = baos.toByteArray();
                }
                return new ByteArrayResource(fileArray);
            }
        };
    }

    public Resource displayImage(String imageUrl) throws IOException {
        log.info("Image Display from " + imageUrl);
        return fileHandler.display(imageUrl);
    }

    public String createImageContentType(String imageUrl) throws IOException {
        return fileHandler.createContentType(imageUrl);
    }
}
