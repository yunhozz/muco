package com.muco.musicservice.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoClientDTO {

    private String id;
    private String email;
    private String age;
    private String nickname;
    private String imageUrl;
}