package com.muco.musicservice.global.dto.request;

import com.muco.musicservice.global.enums.SearchCondition;
import jakarta.validation.constraints.NotBlank;

public record SearchRequestDTO(
        @NotBlank
        String keyword,
        String genre,
        SearchCondition searchCondition
) {}