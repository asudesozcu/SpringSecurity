package com.gmailsystem.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("graphql-layer", r -> r
                        .path("/graphql/**", "/emails/**").filters(f -> f
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(IdResolver())

                                )
                        )
                        .uri("http://localhost:8080")) //graphql port
                .build();
    }

    @Bean
    public KeyResolver IdResolver() {
        return exchange -> {


            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                System.out.println("Gateway " + authHeader);

                return Mono.just(authHeader);
            } else if (exchange.getRequest().getCookies().getFirst("JSESSIONID") != null) {
                System.out.println("Gateway " + exchange.getRequest().getCookies().getFirst("JSESSIONID"));
                return Mono.just(exchange.getRequest().getCookies().getFirst("JSESSIONID").getValue());
            }


            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            System.out.println("ip " + ip);
            return Mono.just("ip:" + ip);

        };
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        // 5 requests per second, with a burst capacity of 10
        return new RedisRateLimiter(2, 4);
    }
}