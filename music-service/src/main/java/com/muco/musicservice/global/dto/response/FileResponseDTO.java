package com.muco.musicservice.global.dto.response;

import org.springframework.core.io.Resource;

public record FileResponseDTO(
        Resource resource,
        String contentType,
        String fileName
) {}
