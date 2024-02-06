package com.muco.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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
            Optional<String> optionalToken = resolveToken(headerToken);
            assert optionalToken.isEmpty();
            return chain.filter(exchange);
        }, -1);
    }

    static class Config {}
}
