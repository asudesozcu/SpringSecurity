package com.gmailsystem.graphql.graphql.service.feignconsumer;

import com.gmailsystem.dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
@FeignClient(name = "gmail-api-server", url = "http://localhost:8086")
public interface FeignEmailClient {

    @GetMapping("/emails/feign")
    List<EmailDto> fetchEmails(@RequestHeader("Cookie") String cookie);}


