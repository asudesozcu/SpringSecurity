package com.example.googlelogin.controller;


import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import com.example.googlelogin.service.GmailService;
import com.example.googlelogin.service.TokenRefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oracle.ucp.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.servlet.View;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class GmailController {

    private final GmailService gmailService;
    private final UserRepository userRepository;
    private final TokenRefreshService tokenRefreshService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    public GmailController(GmailService gmailService, UserRepository userRepository, TokenRefreshService tokenRefreshService, View error) {
        this.gmailService = gmailService;
        this.userRepository = userRepository;
        this.tokenRefreshService = tokenRefreshService;
    }
    @GetMapping("/emails")
    public Mono<ResponseEntity<List<String>>> getEmails(HttpServletRequest request) {

        String email = (String) request.getSession().getAttribute("email");
        System.out.println("Session ID at /emails: " + request.getSession().getId());
        System.out.println("Email: " + request.getSession().getAttribute("email"));

        if (email == null) {
            return Mono.error(new RuntimeException("Kullanıcı oturumu bulunamadı."));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Veritabanında kullanıcı bulunamadı"));

        return gmailService
                .fetchLatestMails(user, clientId, clientSecret)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientRequestException.class, e -> {
                    System.err.println("WebClient error: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> handleGoogleCallback(@RequestParam("code") String code,
                                                       HttpServletRequest request) {
        System.out.println("Gelen code: " + code);

        String redirectUri = "http://localhost:8080/oauth2/callback";

        Map<String, String> tokenMap = tokenRefreshService.getTokensFromAuthCode(
                clientId, clientSecret, redirectUri, code
        );

        String accessToken = tokenMap.get("access_token");

        return ResponseEntity.ok("");
    }

}
