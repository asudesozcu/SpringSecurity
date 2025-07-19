package com.example.googlelogin.service;

import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import dto.EmailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


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

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private final WebClient webClient;
    private final TokenRefreshService tokenRefreshService;
    private final UserRepository userRepository;

    public GmailService(WebClient.Builder builder, TokenRefreshService tokenRefreshService, UserRepository userRepository) {
        this.webClient = builder.baseUrl("https://gmail.googleapis.com/gmail/v1").build();
        this.tokenRefreshService = tokenRefreshService;
        this.userRepository = userRepository;
    }

    public Mono<List<String>> fetchLatestMails(User user, String clientId, String clientSecret) {
        String cacheKey = "userıd:" + user.getId();

        System.out.println("user.getAccessToken() service :" + user.getAccessToken());
        System.out.println("user.getrefreshedToken() service :" + user.getRefreshToken());

        return redisTemplate.opsForValue().get(cacheKey)
                .map(obj -> {
                    if (obj instanceof List<?>) {
                        return ((List<?>) obj).stream()
                                .map(String::valueOf)
                                .toList();
                    }
                    return List.<String>of();
                })

                .switchIfEmpty(getMessageIds(user.getAccessToken(), 10)
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(id -> getEmailDetails(user.getAccessToken(), id))
                        .collectList()
                        .flatMap(fetched -> {
                            System.out.println("get from Gmail Api");
                            return redisTemplate.opsForValue()
                                    .set(cacheKey, fetched, Duration.ofMinutes(10))
                                    .thenReturn(fetched);
                        })
                )
                .onErrorResume(error -> {
                    if (error instanceof WebClientResponseException webClientException &&
                            webClientException.getStatusCode() == HttpStatus.UNAUTHORIZED) {

                        System.out.println("Access token expired. Refreshing");

                        return Mono.fromCallable(() -> {
                                    Map<String, String> refreshedTokens = tokenRefreshService.refreshAccessToken(
                                            clientId, clientSecret, user.getRefreshToken());

                                    String newAccessToken = refreshedTokens.get("access_token");
                                    String newRefreshToken = refreshedTokens.getOrDefault("refresh_token", user.getRefreshToken());

                                    user.setAccessToken(newAccessToken);
                                    user.setRefreshToken(newRefreshToken);
                                    userRepository.save(user);

                                    return newAccessToken;
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(newAccessToken ->
                                        getMessageIds(newAccessToken, 10)
                                                .flatMapMany(Flux::fromIterable)
                                                .flatMap(id -> getEmailDetails(newAccessToken, id))
                                                .collectList()
                                );
                    }
                    return Mono.error(error);
                });


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
                .map(response -> {
                    List<Map<String, String>> messages = (List<Map<String, String>>) response.get("messages");
                    if (messages == null) return List.of();
                    return messages.stream()
                            .map(m -> m.get("id"))
                            .toList();
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
                    event.setLabels(labels);

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

}