package com.muco.musicservice.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

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
        Set<String> genres,
        String lyrics,
        MultipartFile music,
        MultipartFile image
) {}