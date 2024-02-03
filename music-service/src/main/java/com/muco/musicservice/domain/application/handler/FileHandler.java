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
    private String[] fileUrls;

    protected void upload(MultipartFile[] files) throws IOException {
        createFileInfo();
        fileUrls = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file != null) {
                originalName = file.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String uuid = UUID.randomUUID().toString();
                savedName = uuid + extension;

                fileUrls[i] = currentDate + "/" + savedName;
                File f = new File(absolutePath + FILE_URL + fileUrls[i]);
                if (!f.exists())
                    f.mkdirs();

                file.transferTo(f);
            }
        }
    }

    protected String createContentType(String fileName) throws IOException {
        Path path = Paths.get(fileUrls[0] + "/" + fileName);
        return Files.probeContentType(path);
    }

    private static void createFileInfo() {
        absolutePath = new File("").getAbsolutePath();
        currentDate = new SimpleDateFormat("yyMMdd").format(new Date());
    }

    protected abstract Resource download(String fileName) throws IOException;
    protected abstract Resource display(String fileName) throws IOException;
}
