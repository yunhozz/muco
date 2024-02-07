package com.muco.apigateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.Optional;

public interface TokenResolver {
    String getHeaderToken(ServerHttpRequest request);
    Optional<String> resolveToken(String token);
    Claims parseToken(String token);
    void checkValidToken(String token);
}
