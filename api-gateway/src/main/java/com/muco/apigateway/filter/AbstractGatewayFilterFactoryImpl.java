package com.muco.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Strings;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractGatewayFilterFactoryImpl<C>
        extends AbstractGatewayFilterFactory<C>
        implements TokenResolver {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public AbstractGatewayFilterFactoryImpl(Class<C> configClass) {
        super(configClass);
    }

    @Override
    public String getHeaderToken(ServerHttpRequest request) {
        log.info("[REQUEST URI] " + request.getURI());
        List<String> headerTokens = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (headerTokens == null || headerTokens.isEmpty()) {
            throw new IllegalArgumentException("Authorization 헤더에 JWT 토큰이 존재하지 않습니다.");
        }
        return headerTokens.get(0);
    }

    @Override
    public Optional<String> resolveToken(String token) {
        return Strings.hasText(token) ? resolveParts(token) : Optional.empty();
    }

    @Override
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public void checkValidToken(String token) {
        try {
            parseToken(token);

        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
            throw e;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 Jwt 서명입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 토큰입니다.");
        }
    }

    private Optional<String> resolveParts(String token) {
        String[] parts = token.split(" ");
        return parts.length == 2 && parts[0].equals("Bearer") ? Optional.ofNullable(parts[1]) : Optional.empty();
    }

    private Key getSecretKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
