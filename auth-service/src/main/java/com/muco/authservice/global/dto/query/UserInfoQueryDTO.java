package com.muco.authservice.global.dto.query;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoQueryDTO {

    private Long id;
    private String email;
    private int age;
    private String nickname;
    private String imageUrl;

    @QueryProjection
    public UserInfoQueryDTO(Long id, String email, int age, String nickname, String imageUrl) {
        this.id = id;
        this.email = email;
        this.age = age;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}