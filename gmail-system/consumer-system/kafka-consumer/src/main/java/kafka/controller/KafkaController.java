package kafka.controller;

import dto.EmailDto;
import kafka.repo.EmailMapper;
import kafka.repo.EmailRepository;
import kafka.service.EmailConsumerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
public class KafkaController {

    private final EmailRepository emailRepository;

    public KafkaController( EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }


    @GetMapping("fetch-emails")
    public List<EmailDto> getAllEmails() {
        return emailRepository.findAll().stream()
                .map(EmailMapper::fromEntitytoDto)
                .collect(Collectors.toList());
    }
}

