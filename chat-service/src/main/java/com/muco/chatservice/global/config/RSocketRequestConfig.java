package com.muco.chatservice.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class RSocketRequestConfig {

    @Bean
    public RSocketRequester getRSocketRequester(RSocketStrategies strategies) {

        return RSocketRequester.builder()
                .rsocketConnector(connector -> connector.reconnect(Retry.backoff(10, Duration.ofMillis(500)))) // 서버 재연결 설정
                .rsocketStrategies(strategies) // 서버와 통신에서 객체(JSON)로 값을 담아 통신하는데 필요한 인코더, 디코더 설정
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON) // JSON으로 통신 타입을 설정
                .tcp("localhost", 7000); // TCP로 통신하기 위한 설정
    }
}
