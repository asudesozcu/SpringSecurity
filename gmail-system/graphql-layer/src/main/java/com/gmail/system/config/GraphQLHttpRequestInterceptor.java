package com.gmail.system.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;
@Component
public class GraphQLHttpRequestInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        if (attributes instanceof ServletRequestAttributes servletAttrs) {
            HttpServletRequest servletRequest = servletAttrs.getRequest();

            System.out.println("✅ ServletRequest bulundu: " + servletRequest.getRequestURI());

            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(contextBuilder ->
                            contextBuilder.of("javax.servlet.http.HttpServletRequest", servletRequest)
                    ).build()
            );
        } else {
            System.out.println("❌ ServletRequest alınamadı (RequestContextHolder null)");
        }

        return chain.next(request);
    }
}