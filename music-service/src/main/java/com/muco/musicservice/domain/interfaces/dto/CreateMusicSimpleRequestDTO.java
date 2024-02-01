package com.muco.musicservice.domain.interfaces.dto;

import com.muco.musicservice.domain.persistence.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record CreateMusicSimpleRequestDTO(
        @NotBlank
        String userId,
        @NotBlank
        String name,
        @NotEmpty
        Set<Genre> genres,
        String lyrics,
        String imageUrl,
        @NotNull
        MultipartFile music,
        MultipartFile image
) {}