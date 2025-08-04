package com.gmailsystem.googlelogin.service;

import com.gmailsystem.googlelogin.model.User;
import com.gmailsystem.googlelogin.repo.UserRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.gmailsystem.dto.EmailDto;

import java.util.List;


@Service
public class KafkaProducerService {

    private final GmailService gmailService;
    private final KafkaTemplate<String, List<EmailDto>> kafkaTemplate;
    private UserRepository repository;

    public KafkaProducerService(GmailService gmailService, KafkaTemplate<String, List<EmailDto>> kafkaTemplate, UserRepository repository) {
        this.gmailService = gmailService;
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
    }



    @KafkaListener(topics = "request-topic", groupId = "server-group")
    public void consumeRequest(@Payload String requestSignal,
                               @Header(KafkaHeaders.RECEIVED_KEY) String correlationId,
                               @Header("Authorization") byte[] authHeaderBytes) {

        String authHeader = new String(authHeaderBytes);
        String token = authHeader.substring(7);

        User user = repository.findByAccessToken(token)
                .orElseThrow(() -> new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Token geçersiz(kafka)")));

        if (user == null) {
            System.err.println(" Unauthorized request: " + correlationId);
            return;
        }

        List<EmailDto> emails = gmailService.fetchEmails(user).block();
        kafkaTemplate.send("response-topic", correlationId, emails);

        System.out.println(" Sent emails for correlationId: " + correlationId);
    }




}


