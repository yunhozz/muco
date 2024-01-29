package com.muco.musicservice.domain.application.handler;

import com.muco.musicservice.domain.persistence.entity.BaseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class FileHandler<E extends BaseEntity, T> {

    protected String fileUrl;

    protected void createSavedPath() {
        String absolutePath = new File("").getAbsolutePath() + "\\";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String currentDate = dateFormat.format(new Date());
        fileUrl = absolutePath + "/" + currentDate;
    }

    protected abstract E upload(MultipartFile file, T in) throws IOException;
    protected abstract Resource download(String fileName) throws IOException;
    protected abstract Resource display(String fileName);
}
