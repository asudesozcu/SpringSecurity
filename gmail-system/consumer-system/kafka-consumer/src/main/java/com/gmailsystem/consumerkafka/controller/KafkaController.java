package com.gmailsystem.consumerkafka.controller;

import com.gmailsystem.dto.EmailDto;
import com.gmailsystem.consumerkafka.repo.EmailRepository;
import com.gmailsystem.consumerkafka.service.EmailConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KafkaController {

    private final EmailRepository emailRepository;
    private final EmailConsumerService emailConsumerService;

    public KafkaController(EmailRepository emailRepository, EmailConsumerService emailConsumerService) {
        this.emailRepository = emailRepository;
        this.emailConsumerService = emailConsumerService;
    }


    @GetMapping("fetch-emails")
    public List<EmailDto> getAllEmails() {
        System.out.println("size:"+ emailRepository.findTop10ByOrderByReceivedAtDesc().size());
       // return emailRepository.findTop10ByOrderByReceivedAtDesc().stream().map(EmailMapper::fromEntitytoDto).collect(Collectors.toList());
return emailConsumerService.getReceivedEmails();

    }
}

