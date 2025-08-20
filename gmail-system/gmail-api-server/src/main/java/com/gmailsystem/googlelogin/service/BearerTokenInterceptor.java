package com.gmailsystem.googlelogin.service;

import com.gmailsystem.googlelogin.model.User;
import com.gmailsystem.googlelogin.repo.UserRepository;
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
        System.out.println("token in googlelogin: "+token);
        System.out.println("auth.h in googlelogin: "+authHeader);
        User user = repository.findByAccessToken(token)
                .orElseThrow(() -> new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Token geçersiz")));
        System.out.println("test");
        Context ctx = Context.current().withValue(USER_CTX_KEY, user);
        return Contexts.interceptCall(ctx, call, headers, next);

    }
}
