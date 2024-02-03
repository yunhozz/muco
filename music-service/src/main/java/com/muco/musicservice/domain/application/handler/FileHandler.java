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

    private static final String FILE_URL = "/music-service/src/main/resources/file/";

    private static String absolutePath;
    private static String currentDate;

    private String originalName;
    private String savedName;
    private String fileUrl;

    private static void createFileInfo() {
        absolutePath = new File("").getAbsolutePath();
        currentDate = new SimpleDateFormat("yyMMdd").format(new Date());
    }

    protected void upload(MultipartFile file) throws IOException {
        createFileInfo();
        if (!file.isEmpty()) {
            originalName = file.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            savedName = uuid + extension;
            fileUrl = currentDate + "/" + savedName;

            File f = new File(absolutePath + FILE_URL + fileUrl);
            if (!f.exists())
                f.mkdirs();

            file.transferTo(f);
        }
    }

    protected String createContentType(String fileName) throws IOException {
        Path path = Paths.get(fileUrl + "/" + fileName);
        return Files.probeContentType(path);
    }

    protected abstract Resource download(String fileName) throws IOException;
    protected abstract Resource display(String fileName) throws IOException;
}
