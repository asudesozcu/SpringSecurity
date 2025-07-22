package kafka.repo;


import kafka.EmailEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EmailRepository extends MongoRepository<EmailEntity, String> {
    EmailEntity findBySender(String sender);

    List<EmailEntity> findTop10ByOrderByReceivedAtDesc();
}
