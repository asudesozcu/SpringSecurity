package com.example.googlelogin.controller;


import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import com.example.googlelogin.service.GmailService;
import com.example.googlelogin.service.KafkaProducerService;
import com.example.googlelogin.service.TokenRefreshService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import dto.EmailDto;
@RestController
public class GmailController {

    private final GmailService gmailService;
    private final UserRepository userRepository;
    private final TokenRefreshService tokenRefreshService;
    private final KafkaProducerService kafkaProducerService ;


    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private Map<String, Instant> lastRequestTime = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_SECONDS = 10; // 30 saniyede 1 kez

    public GmailController(GmailService gmailService, UserRepository userRepository, TokenRefreshService tokenRefreshService,  KafkaProducerService kafkaProducerService) {
        this.gmailService = gmailService;
        this.userRepository = userRepository;
        this.tokenRefreshService = tokenRefreshService;
        this.kafkaProducerService = kafkaProducerService;
    }
    @GetMapping("/emails")
    public Mono<ResponseEntity<List<EmailDto>>> getEmails(HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("email");
        if (email == null) {
            return Mono.error(new RuntimeException("Kullanıcı oturumu bulunamadı."));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Veritabanında kullanıcı bulunamadı"));

        Instant now = Instant.now();
        Instant last = lastRequestTime.get(email);

        if (last != null && now.isBefore(last.plusSeconds(RATE_LIMIT_SECONDS))) {
            System.out.println("delayed");
            return Mono.delay(Duration.ofSeconds(5))
                    .then(gmailService.fetchLatestMails(user, clientId, clientSecret))
                    .map(rawList -> {
                        List<EmailDto> dtoList = rawList.stream()
                                .map(gmailService::parseEmailString)
                                .collect(Collectors.toList());
                        return ResponseEntity.ok(dtoList);
                    });
        }

        lastRequestTime.put(email, now);

        return gmailService.fetchLatestMails(user, clientId, clientSecret)
                .map(rawList -> {
                    List<EmailDto> dtoList = rawList.stream()
                            .map(gmailService::parseEmailString)
                            .collect(Collectors.toList());
                    System.out.println("size1: "+dtoList.size());
                    return ResponseEntity.ok(dtoList);
                })
                .onErrorResume(WebClientRequestException.class, e -> {
                    System.err.println("WebClient error: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @GetMapping("/emails/publish")
    public Mono<ResponseEntity<List<EmailDto>>> getandpublishEmails(HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("email");
        if (email == null) {
            return Mono.error(new RuntimeException("Kullanıcı oturumu bulunamadı."));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Veritabanında kullanıcı bulunamadı"));

        return gmailService.fetchLatestMails(user, clientId, clientSecret)
                .map(rawList -> {

                    List<EmailDto> dtoList = rawList.stream()
                            .map(gmailService::parseEmailString)
                            .collect(Collectors.toList());

                    dtoList.forEach(kafkaProducerService::publish);
                    System.out.println("size2: "+ dtoList.size());

                    return ResponseEntity.ok(dtoList);
                });
    }


    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("code") String code, HttpServletRequest request) {
        System.out.println("Gelen code: " + code);

        String redirectUri = "http://localhost:8080/oauth2/callback";

        Map<String, String> tokenMap = tokenRefreshService.getTokensFromAuthCode(
                clientId, clientSecret, redirectUri, code
        );

        String accessToken = tokenMap.get("access_token");

        return ResponseEntity.ok("");
    }

}
