package com.muco.authservice.global.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

    private String id;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long atkValidTime;
    private Long rtkValidTime;
}