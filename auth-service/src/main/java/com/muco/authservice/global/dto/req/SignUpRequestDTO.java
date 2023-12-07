package com.muco.authservice.global.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password1;

    @NotBlank
    private String password2;

    @NotBlank
    private String name;

    @NotNull
    private int age;

    @NotBlank
    private String nickname;

    private String imageUrl;
}