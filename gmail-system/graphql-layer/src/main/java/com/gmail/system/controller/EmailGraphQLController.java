package com.gmail.system.controller;

import com.gmail.system.service.FeignEmailClient;
import com.gmail.system.service.GrpcEmailClient;
import com.gmail.system.service.KafkaEmailService;
import dto.EmailDto;

import dto.enums;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


@Controller
public class EmailGraphQLController {

    private final FeignEmailClient feignEmailClient;
    private final GrpcEmailClient grpcEmailClient;
//    private final KafkaEmailService kafkaEmailService;

    public EmailGraphQLController(FeignEmailClient feignEmailClient, GrpcEmailClient grpcEmailClient) {
        this.feignEmailClient = feignEmailClient;
        this.grpcEmailClient = grpcEmailClient;
   //     this.kafkaEmailService = kafkaEmailService;
    }


    @QueryMapping
    public List<EmailDto> getEmails(@Argument enums.CommunicationProtocol protocol,
                                    DataFetchingEnvironment environment) {
        System.out.println("içerdeyiz");
        HttpServletRequest request = environment.getGraphQlContext().get("javax.servlet.http.HttpServletRequest");

        if (request == null) {
            throw new IllegalStateException("HttpServletRequest not found in context");
        }

// Artık güvenle kullanabilirsin
        String cookieHeader = request.getHeader("Cookie");
        System.out.println(cookieHeader);

        if (request == null) {
            throw new IllegalStateException("HttpServletRequest not found in context");
        }



        switch (protocol) {
            case GRPC:
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new RuntimeException("Missing or invalid Authorization header for gRPC source");
                }
                String token = authHeader.substring("Bearer ".length());
                return grpcEmailClient.fetchEmails(token);

            case REST:
                String cookie = request.getHeader("Cookie");
                if (cookie == null) throw new RuntimeException("Missing Cookie header for Feign source");
                try {
                    return feignEmailClient.fetchEmails(cookie);
                } catch (Exception ex) {
                    ex.printStackTrace(); // veya logger.error("Hata:", ex);
                    throw new RuntimeException("Email fetch işlemi başarısız: " + ex.getMessage());
                }

            case KAFKA:
                // return kafkaEmailService.fetchEmails();
                throw new UnsupportedOperationException("Kafka not implemented");

            default:
                throw new IllegalArgumentException("Invalid protocol");
        }
    }
}


