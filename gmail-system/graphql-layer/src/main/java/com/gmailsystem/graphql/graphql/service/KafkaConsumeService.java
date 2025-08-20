package com.gmailsystem.graphql.graphql.service;

import com.gmailsystem.dto.EmailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class KafkaConsumeService {
    public static Map<String, List<EmailDto>> byCorrelation = new ConcurrentHashMap<>();

    public List<EmailDto> getByCorrelationId(String correlationId) {
        return byCorrelation.getOrDefault(correlationId, Collections.emptyList());
    }

    public void add(String correlationId, List<EmailDto> emails) {
        if (emails == null || emails.isEmpty()) return;
        byCorrelation
                .computeIfAbsent(correlationId, k -> new CopyOnWriteArrayList<>())
                .addAll(emails);
    }

    public void clear(String id) { byCorrelation.remove(id); }

    @KafkaListener(
            topics = "response-topic",
            groupId = "client-group"
    )
    public void consumeResponse(@Payload List<EmailDto> emails,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        add(key, emails);
        System.out.println("Response for correlationId " + key + ": " + emails);
    }

    /*
    private final List<EmailDto> receivedEmails = new CopyOnWriteArrayList<>();


    public List<EmailDto> getReceivedEmails() {
        return receivedEmails;
    }

    @KafkaListener(
            topics = "response-topic",
            groupId = "client-group",
            containerFactory = "emailKafkaListenerContainerFactory"
    )
    public void consume(EmailDto event) {
        System.out.println("Event received: " + event.getSubject());
        System.out.println(" From: " + event.getSender());
        receivedEmails.add(event);
        sendFeedback(event);
    }    public void sendFeedback(EmailDto event) {
        System.out.println(" Geri bildirim gönderildi: " + event.getSender() +
                " adresine, '" + event.getSubject() + "' başlıklı e-posta alındı bildirimi yollandı.");

        // Örn: REST çağrısı da yapılabilir (başka bir mikroservise)
        // restTemplate.postForObject("http://notification-service/send", event, Void.class);
    }



*/
}