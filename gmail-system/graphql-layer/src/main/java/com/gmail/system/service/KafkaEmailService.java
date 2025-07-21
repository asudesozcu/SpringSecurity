package com.gmail.system.service;

import dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "kafka-consumer", url = "http://localhost:8084")
public interface KafkaEmailService {

        @GetMapping("/fetch-emails")
        List<EmailDto> fetchEmails();
    }



