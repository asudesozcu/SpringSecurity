package com.gmail.system.controller;

import com.gmail.system.service.FeignEmailClient;
import com.gmail.system.service.GrpcEmailClient;
import com.gmail.system.service.KafkaEmailService;
import dto.EmailDto;

import dto.enums.CommunicationProtocol;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


@Controller
public class EmailGraphQLController {

    private final FeignEmailClient feignEmailClient;
    private final GrpcEmailClient grpcEmailClient;
    private final KafkaEmailService kafkaEmailService ;

    public EmailGraphQLController(FeignEmailClient feignEmailClient, GrpcEmailClient grpcEmailClient, KafkaEmailService kafkaEmailService) {
        this.feignEmailClient = feignEmailClient;
        this.grpcEmailClient = grpcEmailClient;
       this.kafkaEmailService = kafkaEmailService;
    }


    @QueryMapping
    public List<EmailDto> getEmails(@Argument CommunicationProtocol protocol,
                                    DataFetchingEnvironment environment) {
        System.out.println("içerdeyiz");
        HttpServletRequest request = environment.getGraphQlContext().get("javax.servlet.http.HttpServletRequest");

        if (request == null) {
            throw new IllegalStateException("HttpServletRequest not found in context");
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader: " + authHeader);
        String token = authHeader.substring(7); // "Bearer " sonrası kısmı al
        System.out.println("token: " + token);


        if (request == null) {
            throw new IllegalStateException("HttpServletRequest not found in context");
        }


        switch (protocol) {
            case GRPC:
               System.out.println("authHeader: " + authHeader);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new RuntimeException("Missing or invalid Authorization header for gRPC source");
                }

                return grpcEmailClient.fetchEmails(authHeader);

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
                try {
                    return kafkaEmailService.fetchEmails();
                }catch (Exception ex) {
                    throw new UnsupportedOperationException("Kafka not implemented");

                }
            default:
                throw new IllegalArgumentException("Invalid protocol");
        }
    }
}


