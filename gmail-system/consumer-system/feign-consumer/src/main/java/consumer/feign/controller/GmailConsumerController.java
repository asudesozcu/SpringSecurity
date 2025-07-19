package consumer.feign.controller;

import consumer.feign.client.GmailApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;
import dto.EmailDto;
@RestController
public class GmailConsumerController {

    private final GmailApiClient gmailApiClient;

    public GmailConsumerController(GmailApiClient gmailApiClient) {
        this.gmailApiClient = gmailApiClient;
    }

    @GetMapping("/consume-emails")
    public ResponseEntity<List<EmailDto>> getEmails(@CookieValue("JSESSIONID") String sessionId) {
        String cookieHeader = "JSESSIONID=" + sessionId;
        System.out.println("COOKIE: " + cookieHeader);
        List<EmailDto>  emails = gmailApiClient.getEmails(cookieHeader);
        return ResponseEntity.ok(emails);

    }
    }


