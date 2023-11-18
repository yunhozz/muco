package com.muco.authservice.global.auth.oauth;

import com.muco.authservice.global.enums.LoginType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2Provider {

    private String email;
    private String name;
    private String imageUrl;
    private String userNameAttributeName;
    private LoginType loginType;
    private Map<String, Object> attributes;

    private OAuth2Provider() {
        super();
    }

    public static OAuth2Provider of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> ofGoogle(userNameAttributeName, attributes);
            case "kakao" -> ofKakao(userNameAttributeName, attributes);
            case "naver" -> ofNaver(userNameAttributeName, attributes);
            default -> new OAuth2Provider();
        };
    }

    private static OAuth2Provider ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Provider.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .imageUrl((String) attributes.get("picture"))
                .userNameAttributeName(userNameAttributeName)
                .loginType(LoginType.GOOGLE)
                .attributes(attributes)
                .build();
    }

    private static OAuth2Provider ofKakao(String userNameAttributeName, Map<String ,Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Provider.builder()
                .email((String) kakaoAccount.get("email"))
                .name((String) kakaoProfile.get("nickname"))
                .imageUrl((String) kakaoProfile.get("profile_img_url"))
                .userNameAttributeName(userNameAttributeName)
                .loginType(LoginType.KAKAO)
                .attributes(attributes)
                .build();
    }

    private static OAuth2Provider ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuth2Provider.builder()
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .imageUrl((String) response.get("profile"))
                .userNameAttributeName(userNameAttributeName)
                .loginType(LoginType.NAVER)
                .attributes(attributes)
                .build();
    }
}