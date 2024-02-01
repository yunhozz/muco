package com.muco.musicservice.domain.application.handler;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
public final class FileHandler {

    private String originalName;
    private String savedName;
    private String[] fileUrls;

    private static final String FILE_URL = "/music-service/src/main/resources/file/";

    private FileHandler() {}

    public void transferFiles(MultipartFile[] files) throws IOException {
        String absolutePath = new File("").getAbsolutePath();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String currentDate = dateFormat.format(new Date());
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
}
