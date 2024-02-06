package com.muco.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AuthorizationHeaderFilter {

    private final AbstractGatewayFilterFactoryImpl abstractGatewayFilterFactory;

    public AuthorizationHeaderFilter() {
        this.abstractGatewayFilterFactory = new AbstractGatewayFilterFactoryImpl() {
            @Override
            public GatewayFilter apply(Config config) {
                return (exchange, chain) -> {
                    ServerHttpRequest request = exchange.getRequest();
                    String headerToken = getHeaderToken(request);
                    Optional<String> optionalToken = resolveToken(headerToken);
                    assert optionalToken.isEmpty();
                    return chain.filter(exchange);
                };
            }
        };
    }

    public GatewayFilter apply(AbstractGatewayFilterFactoryImpl.Config config) {
        GatewayFilter filter = abstractGatewayFilterFactory.apply(config);
        return new OrderedGatewayFilter(filter, -1);
    }
}
