package com.muco.musicservice.global.dto.response;

import com.muco.musicservice.global.enums.SearchCategory;

import java.util.List;

public record SearchResultResponseDTO(
        SearchCategory category,
        List<? extends SearchResponseDTO> searchResults
) {}