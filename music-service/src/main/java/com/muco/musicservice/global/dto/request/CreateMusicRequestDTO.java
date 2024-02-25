package com.muco.musicservice.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Builder
public record CreateMusicRequestDTO(
        @NotBlank
        String musicName,
        @NotEmpty
        Set<String> genres,
        String lyrics,
        MultipartFile music,
        MultipartFile image
) {}