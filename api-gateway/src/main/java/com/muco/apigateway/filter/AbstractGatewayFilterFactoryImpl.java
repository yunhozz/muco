package com.muco.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractGatewayFilterFactoryImpl extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactoryImpl.Config> {

    public abstract GatewayFilter apply(Config config);

    protected String getHeaderToken(ServerHttpRequest request) {
        log.info("[REQUEST URI] " + request.getURI());
        List<String> headerTokens = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        return headerTokens.get(0);
    }

    protected Optional<String> resolveToken(String token) {
        return Strings.hasText(token) ? resolveParts(token) : Optional.empty();
    }

    protected Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Optional<String> resolveParts(String token) {
        String[] parts = token.split(" ");
        return parts.length == 2 && parts[0].equals("Bearer") ? Optional.ofNullable(parts[1]) : Optional.empty();
    }

    public static class Config {}
}
