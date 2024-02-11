package com.muco.musicservice.domain.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record CreateMusicSimpleRequestDTO(
        @NotBlank
        String name,
        @NotEmpty
        Set<String> genres,
        String lyrics,
        @NotNull
        MultipartFile music,
        MultipartFile image
) {}