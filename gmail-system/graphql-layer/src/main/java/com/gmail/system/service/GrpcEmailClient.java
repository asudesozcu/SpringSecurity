package com.gmail.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.EmailRequest;
import common.EmailResponse;
import common.EmailServiceGrpc;
import dto.EmailDto;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
@FeignClient(name = "grpc-consumer", url = "http://localhost:8083")
public interface GrpcEmailClient {

        @GetMapping("/fetch-mails")
        List<EmailDto> fetchEmails(@RequestHeader("Authorization") String token);
    }
