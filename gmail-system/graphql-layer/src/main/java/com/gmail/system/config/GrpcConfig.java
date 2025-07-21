package com.gmail.system.config;

import common.EmailServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public ManagedChannel emailServiceChannel() {
        return ManagedChannelBuilder
                .forAddress("localhost", 9090) //
                .usePlaintext()
                .build();
    }

    @Bean
    public EmailServiceGrpc.EmailServiceBlockingStub emailServiceStub(ManagedChannel channel) {
        return EmailServiceGrpc.newBlockingStub(channel);
    }
}
