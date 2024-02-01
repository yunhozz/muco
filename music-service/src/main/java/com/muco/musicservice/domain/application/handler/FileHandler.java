package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.BaseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public abstract class FileHandler<E extends BaseEntity, T> {

    protected String originalName;
    protected String savedName;
    protected String[] fileUrls;

    private static final String FILE_URL = "/music-service/src/main/resources/file/";

    protected void transferFiles(MultipartFile[] files) throws IOException {
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

    public abstract E upload(T in) throws IOException;
    public abstract Resource download(String fileName) throws IOException;
    public abstract Resource display(String fileName) throws IOException;
    public abstract String createContentType(String fileName) throws IOException;
    protected abstract Path getPath(String fileName);
}
