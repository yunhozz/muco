package com.muco.musicservice.domain.application.handler;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public abstract class FileHandler {

    private static final String CURRENT_DATE = new SimpleDateFormat("yyMMdd").format(new Date());
    private static final String ABSOLUTE_PATH = new File("").getAbsolutePath();
    private static final String ROOT_DIRECTORY = "/music-service/src/main/resources/file/";
    protected static final String FILE_DIRECTORY = ABSOLUTE_PATH + ROOT_DIRECTORY;

    private String originalName;
    private String savedName;
    private String fileUrl;

    protected void upload(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            savedName = uuid + extension;
            fileUrl = CURRENT_DATE + "/" + savedName;

            File f = new File(FILE_DIRECTORY + fileUrl);
            if (!f.exists())
                f.mkdirs();

            file.transferTo(f);
        }
    }

    protected String createContentType(String fileUrl) throws IOException {
        Path path = getPath(fileUrl);
        return Files.probeContentType(path);
    }

    protected Path getPath(String fileUrl) {
        return Paths.get(FILE_DIRECTORY + fileUrl);
    }

    protected abstract Resource download(String fileUrl) throws IOException;
    protected abstract Resource display(String fileUrl) throws IOException;
}
