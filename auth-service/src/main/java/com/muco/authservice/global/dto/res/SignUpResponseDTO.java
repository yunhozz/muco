package com.muco.authservice.global.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDTO {

    private Long id;
    private String email;
    private String code;
}