package com.muco.musicservice.global.dto.request;

import com.muco.musicservice.persistence.entity.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateMusicRequestDTO(
        @NotNull
        Long userId,
        @NotBlank
        String email,
        int age,
        @NotBlank
        String nickname,
        String userImageUrl,
        @NotBlank
        String musicName,
        @NotEmpty
        List<Genre> genres,
        String lyrics,
        String musicImageUrl
) {}