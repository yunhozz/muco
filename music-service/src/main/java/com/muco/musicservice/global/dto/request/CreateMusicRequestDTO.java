package com.muco.musicservice.global.dto.request;

import com.muco.musicservice.persistence.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CreateMusicRequestDTO {

    private Long userId;

    private String email;

    private int age;

    private String nickname;

    private String userImageUrl;

    private String musicName;

    private List<Genre> genres;

    private String lyrics;

    private String musicImageUrl;

    private CreateMusicRequestDTO() {
        super();
    }
}