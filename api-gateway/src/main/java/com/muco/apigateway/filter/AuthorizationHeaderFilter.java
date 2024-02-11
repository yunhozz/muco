package com.muco.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactoryImpl<AuthorizationHeaderFilter.Config> {

    private static final String JWT_TOKEN_REFRESH_URL = "http://localhost:8000/api/auth/token/reissue";

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            Optional<String> optToken = getAccessToken(request);
            if (optToken.isPresent()) {
                String token = optToken.get();
                try {
                    checkValidToken(token);
                    Claims claims = parseToken(token);
                    ServerHttpRequest requestWithSubject = request.mutate()
                            .header("sub", claims.getSubject())
                            .build();
                    return chain.filter(exchange.mutate().request(requestWithSubject).build());

                } catch (ExpiredJwtException e) {
                    HttpCookie cookie = request.getCookies().getFirst("username");
                    if (cookie != null) {
                        byte[] decode = Base64.getUrlDecoder().decode(cookie.getValue());
                        String username = (String) SerializationUtils.deserialize(decode);
                        URI redirectUri = UriComponentsBuilder.fromUriString(JWT_TOKEN_REFRESH_URL)
                                .queryParam("username", username)
                                .build().toUri();

                        WebClient webClient = WebClient.create();
                        return webClient.post()
                                .uri(redirectUri)
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(m -> {
                                    ServerHttpResponse response = exchange.getResponse();
                                    DataBuffer buffer = response.bufferFactory().wrap(m.getBytes());
                                    response.setStatusCode(HttpStatus.CREATED);
                                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    return response.writeWith(Mono.just(buffer));
                                });
                    }
                }
            }
            return chain.filter(exchange);
        }, -1);
    }

    static class Config {}
}
