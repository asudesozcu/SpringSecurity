package com.gmailsystem.googlelogin.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
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


            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(contextBuilder ->
                            contextBuilder.of("javax.servlet.http.HttpServletRequest", servletRequest)
                    ).build()
            );
        }

        return chain.next(request);
    }
}