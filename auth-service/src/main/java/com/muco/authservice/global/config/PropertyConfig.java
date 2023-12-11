package com.muco.authservice.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(PropertyConfig.MucoProperties.class)
public class PropertyConfig {

    @Getter
    @RefreshScope
    @ConfigurationProperties(prefix = "muco")
    public static class MucoProperties {
        private final Jwt jwt = new Jwt();
        private final OAuth2 oAuth2 = new OAuth2();
        private final Redis redis = new Redis();
        private final Mail mail = new Mail();

        @Getter
        @Setter
        public static class Jwt {
            private String secretKey;
            private String tokenType;
            private Long accessTokenValidTime;
            private Long refreshTokenValidTime;
        }

        @Getter
        public static class OAuth2 {
            private List<String> authorizedRedirectUris = new ArrayList<>();
            public OAuth2 setAuthorizedRedirectUris(List<String> authorizedRedirectUris) {
                this.authorizedRedirectUris = authorizedRedirectUris;
                return this;
            }
        }

        @Getter
        @Setter
        public static class Redis {
            private String host;
            private int port;
        }

        @Getter
        @Setter
        public static class Mail {
            private String host;
            private int port;
            private String username;
            private String password;
        }
    }
}