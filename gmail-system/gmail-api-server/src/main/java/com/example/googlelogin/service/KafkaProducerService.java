package com.example.googlelogin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import dto.EmailDto;


@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, EmailDto> kafkaTemplate;
    private static final String TOPIC = "email-events";

    public void publish(EmailDto event) {
        kafkaTemplate.send(TOPIC, event)
                .thenAccept(result -> System.out.println("Sent: " + event.getSubject()))
                .exceptionally(ex -> {
                    System.err.println(" Kafka send failed: " + ex.getMessage());
                    return null;
                });

    }




}


