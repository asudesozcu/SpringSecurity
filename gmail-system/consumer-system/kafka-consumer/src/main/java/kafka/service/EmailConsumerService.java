package kafka.service;

import kafka.EmailEntity;
import kafka.repo.EmailMapper;
import kafka.repo.EmailRepository;
import org.springframework.kafka.annotation.KafkaListener;
import dto.EmailDto;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumerService {
    private final EmailRepository emailRepository;

    public EmailConsumerService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
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

        // MongoDB'ye kayıt

        try {
            emailRepository.save(entity);
            System.out.println("✅ Veri MongoDB’ye kaydedildi.");
        } catch (Exception e) {
            System.out.println("❌ MongoDB’ye veri yazılamadı:");
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
