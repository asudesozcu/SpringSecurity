package com.example.googlelogin.controller;

import com.example.googlelogin.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import events.EmailFetchedEvent;
@RestController
public class KafkaController {


    private final KafkaProducerService kafkaProducerService;
    @Autowired
    public KafkaController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/publish-email-event") //post kullanımı manuel test için
    public ResponseEntity<String> publishEmailEvent(@RequestBody EmailFetchedEvent  event) {
        kafkaProducerService.publish(event);
        return ResponseEntity.ok("✅ EmailFetchedEvent successfully published.");
    }
}
