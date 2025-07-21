package com.gmail.system.service;

import dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
@FeignClient(name = "grpc-consumer", url = "http://localhost:8083")
public interface GrpcEmailClient {

        @GetMapping("/fetch-mails")
        List<EmailDto> fetchEmails(@RequestHeader("Authorization") String authHeader);
    }
