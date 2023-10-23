package com.muco.authservice.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {

    GOOGLE("구글 사용자"),
    KAKAO("카카오 사용자"),
    NAVER("네이버 사용자"),
    LOCAL("일반 사용자")

    ;

    private final String description;
}