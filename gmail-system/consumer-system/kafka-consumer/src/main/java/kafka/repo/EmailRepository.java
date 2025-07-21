package kafka.repo;


import kafka.EmailEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface EmailRepository extends MongoRepository<EmailEntity, String> {
    EmailEntity findBySender(String sender);
}
