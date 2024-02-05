package com.muco.musicservice.domain.interfaces.dto;

public record UserInfoClientDTO(
        String id,
        String email,
        String age,
        String nickname,
        String imageUrl
) {}