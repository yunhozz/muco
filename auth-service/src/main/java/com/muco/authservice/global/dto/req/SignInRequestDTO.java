package com.muco.authservice.global.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}