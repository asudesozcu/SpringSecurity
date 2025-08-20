package com.gmailsystem.googlelogin.service;

import com.gmailsystem.googlelogin.model.User;
import com.gmailsystem.googlelogin.repo.UserRepository;
import com.gmailsystem.dto.EmailDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GmailService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

        private final ReactiveRedisTemplate<String, Object> redisTemplate;
        private final WebClient webClient;
        private final TokenRefreshService tokenRefreshService;
        private final UserRepository userRepository;

        public GmailService(ReactiveRedisTemplate<String, Object> redisTemplate,
                            WebClient.Builder builder,
                            TokenRefreshService tokenRefreshService,
                            UserRepository userRepository) {
            this.redisTemplate = redisTemplate;
            this.webClient = builder.baseUrl("https://gmail.googleapis.com/gmail/v1").build();
            this.tokenRefreshService = tokenRefreshService;
            this.userRepository = userRepository;

    }

    public Mono<List<EmailDto>> fetchEmails(User user ) {
        return fetchLatestMails(user, clientId, clientSecret)
                .map(rawList -> rawList.stream()
                        .map(this::parseEmailString)
                        .collect(Collectors.toList()))
                .onErrorResume(WebClientRequestException.class, e -> {
                    System.err.println("WebClient error: " + e.getMessage());
                    return Mono.error(e);
                });
    }


    public Mono<List<String>> fetchLatestMails(User user, String clientId, String clientSecret) {
        String cacheKey = "userıd:" + user.getId();

        System.out.println("user.getAccessToken() service :" + user.getAccessToken());
        System.out.println("user.getrefreshedToken() service :" + user.getRefreshToken());

        return redisTemplate.opsForValue().get(cacheKey)
                .onErrorResume(e -> {
                        System.err.println("REDİS CACHE failed for messageId: "  +
                            " | Error: " + e.getClass().getName() + " - " + e.getMessage());
                    e.printStackTrace();
                    return Mono.error(e);
                })

                .map(obj -> {
                    if (obj instanceof List<?>) {
                        return ((List<?>) obj).stream()
                                .map(String::valueOf)
                                .toList();
                    }
                    return List.<String>of();
                })

                .switchIfEmpty(getMessageIds(user.getAccessToken(), 10)
                        .onErrorResume(WebClientResponseException.Unauthorized.class, ex -> {
                            System.out.println(" Access token expired. Trying refresh");
                            return Mono.fromCallable(() -> {
                                        Map<String, String> refreshedTokens = tokenRefreshService.refreshAccessToken(
                                                clientId, clientSecret, user.getRefreshToken());

                                        String newAccessToken = refreshedTokens.get("access_token");
                                        String newRefreshToken = refreshedTokens.getOrDefault("refresh_token", user.getRefreshToken());

                                        user.setAccessToken(newAccessToken);
                                        user.setRefreshToken(newRefreshToken);
                                        userRepository.save(user);

                                        System.out.println(" Refreshed Token: " + newAccessToken);
                                        return newAccessToken;
                                    })
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .flatMap(newAccessToken -> getMessageIds(newAccessToken, 10));
                        })
                        .onErrorResume(WebClientRequestException.class, ex -> {
                            System.err.println(" Connection reset in getMessageIds! Probably network or SSL issue.");
                            ex.printStackTrace();
                            return Mono.error(ex);
                        })

                        .flatMapMany(Flux::fromIterable)
                        .flatMap(id -> getEmailDetails(user.getAccessToken(), id))
                        .collectList()
                        .flatMap(fetched -> {
                            System.out.println("get from Gmail Api");
                            return redisTemplate.opsForValue()
                                    .set(cacheKey, fetched, Duration.ofMinutes(10))
                                    .thenReturn(fetched);
                        })
                );



    }

    private Mono<List<String>> getMessageIds(String accessToken, int maxResults) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/messages")
                        .queryParam("maxResults", maxResults)
                        .build())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))) // Retry ekle
                .timeout(Duration.ofSeconds(5)) // Connection reset önlemek için
                .map(response -> {
                    List<Map<String, String>> messages = (List<Map<String, String>>) response.get("messages");
                    if (messages == null) return List.of();
                    return messages.stream().map(m -> m.get("id")).toList();
                });
    }



    private Mono<String> getEmailDetails(String accessToken, String messageId) {
        return webClient.get()
                .uri("/users/me/messages/{id}?format=metadata&metadataHeaders=Subject&metadataHeaders=From", messageId)
                .headers(headers -> headers.setBearerAuth(String.valueOf(accessToken)))
                .retrieve()
                .bodyToMono(Map.class)
                .map(msg -> {

                    List<Map<String, String>> headers = (List<Map<String, String>>)
                            ((Map<String, Object>) msg.get("payload")).get("headers");

                    List<String> labelIds = (List<String>) msg.get("labelIds");

                    String subject = headers.stream()
                            .filter(h -> h.get("name").equalsIgnoreCase("Subject"))
                            .findFirst().map(h -> h.get("value")).orElse("No Subject");

                    String from = (headers.stream()
                            .filter(h -> h.get("name").equalsIgnoreCase("From"))
                            .findFirst().map(h -> h.get("value")).orElse("Unknown"));
                    boolean hasAttachment = ((Map<String, Object>) msg.get("payload"))
                            .get("mimeType").toString().contains("multipart");

                    int sizeEstimate = (int) msg.getOrDefault("sizeEstimate", 0);

                    long internalDate = Long.parseLong(msg.get("internalDate").toString());
                    LocalDateTime receivedAt = Instant.ofEpochMilli(internalDate)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    String emailId = (String) msg.get("id");

                    String snippet = (String) msg.getOrDefault("snippet", "");
                    return "Email ID: " + emailId +
                            " | Labels: " + labelIds +
                            " | From: " + from +
                            " | Subject: " + subject +
                            " | Snippet: " + snippet +
                            " | Received At: " + receivedAt +
                            " | Has Attachment: " + hasAttachment +
                            " | Size Estimate: " + sizeEstimate;
                });
    }


    public EmailDto parseEmailString(String raw) {
        EmailDto event = new EmailDto();

        try {
            String[] parts = raw.split("\\|");

            for (String part : parts) {
                part = part.trim();

                if (part.startsWith("Email ID:")) {
                    event.setEmailId(part.replace("Email ID:", "").trim());

                } else if (part.startsWith("Labels:")) {
                    String labelsStr = part.replace("Labels:", "").trim()
                            .replace("[", "").replace("]", "");
                    List<String> labels = Arrays.stream(labelsStr.split(","))
                            .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                    event.setLabelIds(labels);

                } else if (part.startsWith("From:")) {
                    event.setSender(part.replace("From:", "").trim());

                } else if (part.startsWith("Subject:")) {
                    event.setSubject(part.replace("Subject:", "").trim());

                } else if (part.startsWith("Snippet:")) {
                    event.setSnippet(part.replace("Snippet:", "").trim());

                } else if (part.startsWith("Received At:")) {
                    String datetime = part.replace("Received At:", "").trim();
                    event.setReceivedAt(LocalDateTime.parse(datetime));

                } else if (part.startsWith("Has Attachment:")) {
                    event.setHasAttachment(Boolean.parseBoolean(part.replace("Has Attachment:", "").trim()));

                } else if (part.startsWith("Size Estimate:")) {
                    event.setSizeEstimate(Integer.parseInt(part.replace("Size Estimate:", "").trim()));
                }
            }

        } catch (Exception e) {
            System.out.println(" Parse error: " + e.getMessage());
        }

        return event;
    }

    public User getUserFromSession(HttpServletRequest request) {
        String email = (String) request.getSession().getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Kullanıcı oturumu bulunamadı.");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Veritabanında kullanıcı bulunamadı"));
    }

}