package com.muco.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GuestFilter extends AbstractGatewayFilterFactoryImpl<GuestFilter.Config> {

    public GuestFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String token = getAccessToken(request).get();
            Claims claims = parseToken(token);
            String auth = (String) claims.get("auth");

            if (!auth.contains("ROLE_GUEST")) {
                log.error("[ACCESS TOKEN IS NOT AUTHORIZED]");
                throw new SignatureException("GUEST 권한을 가지고 있지 않습니다. auth = " + auth);
            }

            log.info("[ACCESS TOKEN IS OK]");
            ServerHttpRequest requestWithUserInfo = createRequestWithUserInfo(request, claims);

            return chain.filter(exchange.mutate().request(requestWithUserInfo).build());
        };
    }

    static class Config {}
}
