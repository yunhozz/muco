package com.muco.musicservice.global.dto.request;

import com.muco.musicservice.global.enums.SearchCategory;
import com.muco.musicservice.global.enums.SearchCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchRequestDTO(
        @NotBlank
        String keyword,
        @NotNull
        SearchCategory category,
        SearchCondition searchCondition
) {}