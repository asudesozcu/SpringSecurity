package com.gmailsystem.consumerkafka.service;

import com.gmailsystem.consumerkafka.EmailEntity;
import com.gmailsystem.consumerkafka.repo.EmailMapper;
import com.gmailsystem.consumerkafka.repo.EmailRepository;
import org.springframework.kafka.annotation.KafkaListener;
import com.gmailsystem.dto.EmailDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class EmailConsumerService {
    private final EmailRepository emailRepository;
    private final List<EmailDto> receivedEmails = new CopyOnWriteArrayList<>();

    public EmailConsumerService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<EmailDto> getReceivedEmails() {
        return receivedEmails;
    }

    @KafkaListener(
            topics = "email-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void consume(EmailDto event) {
        System.out.println("Event received: " + event.getSubject());
        System.out.println(" From: " + event.getSender());
        // DTO → Entity dönüşümü
        EmailEntity entity = EmailMapper.fromDtotoEntity(event);
        receivedEmails.add(event);
        // MongoDB'ye kayıt

        try {
            emailRepository.save(entity);
            System.out.println(" dbye kaydedildi.");
        } catch (Exception e) {
            System.out.println("dbye yazılamadı:");
            e.printStackTrace();
        }

        sendFeedback(event);

    }


    public void sendFeedback(EmailDto event) {
        System.out.println(" Geri bildirim gönderildi: " + event.getSender() +
                " adresine, '" + event.getSubject() + "' başlıklı e-posta alındı bildirimi yollandı.");

        // Örn: REST çağrısı da yapılabilir (başka bir mikroservise)
        // restTemplate.postForObject("http://notification-service/send", event, Void.class);
    }
}
