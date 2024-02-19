package com.muco.musicservice.domain.application.handler;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceHandler {

    void upload(MultipartFile file) throws IOException;
    Resource download(String fileUrl) throws IOException;
    Resource display(String fileUrl) throws IOException;
    String createContentType(String fileUrl) throws IOException;
}
