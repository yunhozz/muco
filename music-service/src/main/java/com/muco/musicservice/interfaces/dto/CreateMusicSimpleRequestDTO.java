package com.muco.musicservice.interfaces.dto;

import com.muco.musicservice.persistence.entity.Genre;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMusicSimpleRequestDTO {

    @NotNull
    private String userId;

    private String name;

    @NotEmpty
    private List<Genre> genres;

    private String lyrics;

    private String imageUrl;
}