package com.muco.musicservice.domain.application.handler;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface FileHandler<E, T> {
    E upload(MultipartFile file, T in);
    Resource download(String fileName);
    Resource display(String fileName);

    default String createSavedPath() {
        String absolutePath = new File("").getAbsolutePath() + "\\";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String currentDate = dateFormat.format(new Date());
        return absolutePath + "/" + currentDate;
    }
}
