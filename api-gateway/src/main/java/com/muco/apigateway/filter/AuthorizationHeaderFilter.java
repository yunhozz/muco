package com.muco.apigateway.filter;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.net.URI;
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
                    URI redirectUri = URI.create("http://localhost:8000/api/auth/token/reissue");
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().setLocation(redirectUri);
                    return response.setComplete();
                }
            }

            return chain.filter(exchange);
        }, -1);
    }

    static class Config {}
}
