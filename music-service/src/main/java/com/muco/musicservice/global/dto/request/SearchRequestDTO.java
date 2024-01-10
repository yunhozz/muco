package com.muco.musicservice.global.dto.request;

import com.muco.musicservice.global.enums.SearchOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDTO {

    @NotBlank
    private String keyword;

    private String genre;

    private SearchOrder searchOrder;
}