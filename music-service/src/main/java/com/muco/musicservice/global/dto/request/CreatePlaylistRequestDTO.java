package com.muco.musicservice.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePlaylistRequestDTO(
        @NotNull
        Long musicId,
        @NotBlank
        String name
) {}
