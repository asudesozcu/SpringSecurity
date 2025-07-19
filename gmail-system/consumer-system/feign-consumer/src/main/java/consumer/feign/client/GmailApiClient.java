package consumer.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import dto.EmailDto;
import java.util.List;

@FeignClient(name = "gmail-api-server", url = "http://localhost:8080")
public interface
GmailApiClient {

    @GetMapping("/emails")
    List<EmailDto> getEmails(@RequestHeader("Cookie") String cookie);}
