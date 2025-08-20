package com.gmailsystem.consumergrpc.controller;


import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.consumergrpc.service.GrpcConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class GrpcConsumerController {

    private final GrpcConsumerService grpcConsumerService;

    public GrpcConsumerController(GrpcConsumerService grpcConsumerService) {
        this.grpcConsumerService = grpcConsumerService;
    }

    @GetMapping("/fetch-mails")
    public List<EmailDto> fetchEmails(@RequestHeader("Authorization") String authHeader) {
        System.out.println("GRPC CONSUMER: "+ authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header eksik veya hatalı");
        }

        String token = authHeader.substring(7); // "Bearer " sonrası kısmı al
        return grpcConsumerService.fetchEmails(token);
    }

}