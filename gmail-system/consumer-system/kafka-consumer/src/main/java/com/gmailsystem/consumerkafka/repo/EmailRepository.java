package com.gmailsystem.consumerkafka.repo;


import com.gmailsystem.consumerkafka.EmailEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmailRepository extends MongoRepository<EmailEntity, String> {
    EmailEntity findBySender(String sender);

    List<EmailEntity> findTop10ByOrderByReceivedAtDesc();
}
