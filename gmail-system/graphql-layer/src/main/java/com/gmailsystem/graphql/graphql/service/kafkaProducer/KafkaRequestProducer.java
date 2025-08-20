package com.gmailsystem.graphql.graphql.service.kafkaProducer;

import com.gmailsystem.dto.EmailDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaRequestProducer  {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaRequestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Map<String,String> sendRequest(String accessToken) {
        String correlationId = UUID.randomUUID().toString();

        ProducerRecord<String, String> record = new ProducerRecord<>("request-topic", correlationId, "REQUEST");
        record.headers().add("Authorization", ("Bearer " + accessToken).getBytes());

        kafkaTemplate.send(record);

        return Map.of("correlationId", correlationId);
    }


//    @KafkaListener(topics = "response-topic", groupId = "client-group",containerFactory = "emailKafkaListenerContainerFactory")
//    public void consumeResponse(@Payload List<EmailDto> emails,
//                                @Header(KafkaHeaders.RECEIVED_KEY) String correlationId) {
//
//      //  System.out.println("Response for correlationId " + correlationId + ": " + emails);
//
//
//
//    }
}
