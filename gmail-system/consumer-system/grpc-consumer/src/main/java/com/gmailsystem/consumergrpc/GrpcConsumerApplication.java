package com.gmailsystem.consumergrpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.gmailsystem.consumergrpc", "com.gmailsystem.logging"})

public class GrpcConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcConsumerApplication.class, args);
    }
}