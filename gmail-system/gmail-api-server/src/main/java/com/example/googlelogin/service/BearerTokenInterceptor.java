package com.example.googlelogin.service;

import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@GrpcGlobalServerInterceptor
@Component
public class BearerTokenInterceptor implements ServerInterceptor {

    @Autowired
    private UserRepository repository;

    public static final Context.Key<User> USER_CTX_KEY = Context.key("user");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Missing bearer token"));
        }

        String token = authHeader.substring(7);
        User user = repository.findByAccessToken(token)
                .orElseThrow(() -> new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Token geçersiz")));

        Context ctx = Context.current().withValue(USER_CTX_KEY, user);
        return Contexts.interceptCall(ctx, call, headers, next);

    }
}
