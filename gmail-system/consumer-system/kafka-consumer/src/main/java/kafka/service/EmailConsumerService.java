package kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import events.EmailFetchedEvent;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumerService {
    public EmailConsumerService() {
        System.out.println("✅ EmailConsumerService loaded.");

    }


    @KafkaListener(
            topics = "email-events",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )    public void consume(EmailFetchedEvent event) {
        System.out.println("Event received: " + event.getSubject());
        System.out.println(" Subject: " + event.getSubject());
        System.out.println(" From: " + event.getSender());
        System.out.println(" Received at: " + event.getReceivedAt());
sendFeedback(event);

    }





    public void sendFeedback(EmailFetchedEvent event) {
        System.out.println("🔔 Geri bildirim gönderildi: " + event.getSender() +
                " adresine, '" + event.getSubject() + "' başlıklı e-posta alındı bildirimi yollandı.");

        // Örn: REST çağrısı da yapılabilir (başka bir mikroservise)
        // restTemplate.postForObject("http://notification-service/send", event, Void.class);
    }
}
