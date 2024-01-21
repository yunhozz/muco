package com.muco.musicservice.interfaces.dto;

public record UserInfoClientDTO(
        String id,
        String email,
        String age,
        String nickname,
        String imageUrl
) {}