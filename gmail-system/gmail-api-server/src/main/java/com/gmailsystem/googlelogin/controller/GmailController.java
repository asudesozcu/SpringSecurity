package com.gmailsystem.googlelogin.controller;


import com.gmailsystem.googlelogin.repo.UserRepository;
import com.gmailsystem.googlelogin.service.GmailService;
import com.gmailsystem.googlelogin.service.KafkaProducerService;
import com.gmailsystem.googlelogin.service.TokenRefreshService;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.gmailsystem.dto.EmailDto;

@RestController
public class GmailController {

    private final GmailService gmailService;
    private final UserRepository userRepository;
    private final TokenRefreshService tokenRefreshService;
    private final KafkaProducerService kafkaProducerService;


    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;


    public GmailController(GmailService gmailService, UserRepository userRepository, TokenRefreshService tokenRefreshService, KafkaProducerService kafkaProducerService) {
        this.gmailService = gmailService;
        this.userRepository = userRepository;
        this.tokenRefreshService = tokenRefreshService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToReactLogin() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/login"))
                .build();
    }
    @GetMapping("/api/token")
    public ResponseEntity<String> getToken(HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("email");
        if (email == null) {
            return ResponseEntity.status(401).body("Session yok");
        }

        System.out.println("TEST CONTROLLLER"+userRepository.findByEmail(email));
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(user.getAccessToken()))
                .orElse(ResponseEntity.status(404).body("Kullanıcı bulunamadı"));
    }

    @GetMapping("/emails/feign")
    public Mono<ResponseEntity<List<EmailDto>>> getEmails(HttpServletRequest request) {
        return Mono.just(gmailService.getUserFromSession(request))
                .flatMap(gmailService::fetchEmails)
                .map(ResponseEntity::ok);
    }


//    @GetMapping("/emails/publish")
//    public Mono<ResponseEntity<List<EmailDto>>> getAndPublishEmails(HttpServletRequest request) {
//        return Mono.just(gmailService.getUserFromSession(request))
//                .flatMap(gmailService::fetchEmails)
//                .map(dtoList -> {
//                    dtoList.forEach(kafkaProducerService::publish);
//                    return ResponseEntity.ok(dtoList);
//                });
//    }

    @QueryMapping
    public Mono<List<EmailDto>> getEmails(DataFetchingEnvironment environment) {
        HttpServletRequest request = environment.getGraphQlContext().get("javax.servlet.http.HttpServletRequest");

        return Mono.just(gmailService.getUserFromSession(request))
                .flatMap(gmailService::fetchEmails);


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
