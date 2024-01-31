package com.muco.musicservice.domain.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateMusicSimpleRequestDTO(
        @NotBlank
        String userId,
        @NotBlank
        String name,
        @NotBlank
        String type,
        @NotNull
        int priority,
        String lyrics,
        String imageUrl,
        MultipartFile file
) {}