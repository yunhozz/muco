package com.muco.musicservice.interfaces.dto;

import com.muco.musicservice.persistence.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateMusicSimpleRequestDTO(
        @NotBlank
        String userId,
        @NotBlank
        String name,
        @NotEmpty
        List<Genre> genres,
        String lyrics,
        String imageUrl
) {}