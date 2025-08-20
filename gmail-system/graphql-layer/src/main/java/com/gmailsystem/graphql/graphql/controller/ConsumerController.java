package com.gmailsystem.graphql.graphql.controller;

import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.graphql.graphql.service.KafkaConsumeService;
import com.gmailsystem.graphql.graphql.service.feignconsumer.FeignEmailClient;
import com.gmailsystem.graphql.graphql.service.graphqlconsumer.GraphqlEmailService;
import com.gmailsystem.graphql.graphql.service.grpcconsumer.GrpcEmailClientService;
import com.gmailsystem.graphql.graphql.service.kafkaProducer.KafkaRequestProducer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ConsumerController {


    private final FeignEmailClient client;
    private final GrpcEmailClientService grpcClient;
    private final GraphqlEmailService graphqlService;
    private final KafkaConsumeService kafkaEmailService;
    private final KafkaRequestProducer kafkaRequestProducer;
    private final KafkaConsumeService kafkaConsumeService;

    public ConsumerController(FeignEmailClient client, GrpcEmailClientService grpcClient, GraphqlEmailService graphqlService, KafkaConsumeService kafkaEmailService, KafkaRequestProducer kafkaRequestProducer, KafkaConsumeService kafkaConsumeService) {
        this.client = client;
        this.grpcClient = grpcClient;
        this.graphqlService = graphqlService;
        this.kafkaEmailService = kafkaEmailService;
        this.kafkaRequestProducer = kafkaRequestProducer;
        this.kafkaConsumeService = kafkaConsumeService;
    }

    //FEIGN
    @GetMapping("/emails")
    public List<EmailDto> getEmails(@RequestHeader("Cookie") String cookie) {
        return client.fetchEmails(cookie);

    }

    //GRPC
    @GetMapping("/fetch-mails")
    public List<EmailDto> fetchEmails(@RequestHeader("Authorization") String authHeader) {

        System.out.println("GRPC CONSUMER: " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header eksik veya hatalı");
        }

        String token = authHeader.substring(7);
        System.out.println("token"+ token);
        return grpcClient.fetchEmails(token);
    }

    @GetMapping("/fetch")
    public Map<String, String> fetchEmailsKafka(@RequestParam String accessToken) {
     return   kafkaRequestProducer.sendRequest(accessToken);
    }

    @GetMapping("/getdata")
    public  List<EmailDto> getEmailsKafka(@RequestParam String correlationId) {
        return kafkaConsumeService.getByCorrelationId(correlationId);
    }

//
//    @GetMapping("fetch-emails")
//    public List<EmailDto> getAllEmails() {
//        return kafkaEmailService.consumeResponse();
//
//    }

    @GetMapping("/graphql-emails")
    public Map<String, Object> getEmailsQL(@RequestHeader("Cookie") String cookie, @RequestParam String fields) {
        return graphqlService.fetchEmailsDynamic(cookie, fields);

    }
}
