package com.muco.musicservice.global.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserInfoRequestDTO(
        @NotBlank
        String userId,
        @NotBlank
        String email,
        int age,
        @NotBlank
        String nickname,
        String userImageUrl
) {}
