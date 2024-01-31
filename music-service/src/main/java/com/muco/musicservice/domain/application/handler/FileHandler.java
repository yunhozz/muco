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
    protected String fileUrl;

    private static final String FILE_URL = "/music-service/src/main/resources/file/";

    protected void transferFile(MultipartFile file) throws IOException {
        originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        savedName = uuid + extension;

        String absolutePath = new File("").getAbsolutePath();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String currentDate = dateFormat.format(new Date());
        fileUrl = absolutePath + FILE_URL + currentDate;

        new File(fileUrl).mkdirs();
        file.transferTo(new File(fileUrl + "/" + savedName));
    }

    public abstract E upload(T in) throws IOException;
    public abstract Resource download(String fileName) throws IOException;
    public abstract Resource display(String fileName) throws IOException;
    public abstract String createContentType(String fileName) throws IOException;
    protected abstract Path getPath(String name);
}
