package com.muco.apigateway.filter;

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

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String headerToken = getHeaderToken(request);
            Optional<String> optToken = resolveToken(headerToken);

            if (optToken.isPresent()) {
                String token = optToken.get();
                try {
                    checkValidToken(token);
                } catch (ExpiredJwtException e) {
                    HttpCookie cookie = request.getCookies().getFirst("user-id");
                    if (cookie != null) {
                        byte[] decode = Base64.getUrlDecoder().decode(cookie.getValue());
                        String userId = (String) SerializationUtils.deserialize(decode);
                        URI redirectUri = UriComponentsBuilder.fromUriString("http://localhost:8000/api/auth/token/reissue")
                                .queryParam("userId", userId)
                                .build().toUri();

                        WebClient webClient = WebClient.create();
                        Mono<String> mono = webClient.post()
                                .uri(redirectUri)
                                .retrieve()
                                .bodyToMono(String.class);

                        return mono.flatMap(m -> {
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
