package com.gmail.system.service;

import dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
@FeignClient(name = "feign-consumer", url = "http://localhost:8081") // feign-consumer portu
public interface FeignEmailClient {

    @GetMapping("/consume-emails")
    List<EmailDto> fetchEmails(@RequestHeader("Cookie") String cookie);
}


