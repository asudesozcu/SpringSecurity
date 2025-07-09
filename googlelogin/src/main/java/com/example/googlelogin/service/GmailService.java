package com.example.googlelogin.service;

import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.apache.commons.lang3.StringEscapeUtils;


import java.util.List;
import java.util.Map;

@Service
public class GmailService {

    private final WebClient webClient;
    private final TokenRefreshService tokenRefreshService;
    private final UserRepository userRepository;

    public GmailService(WebClient.Builder builder, TokenRefreshService tokenRefreshService, UserRepository userRepository) {
        this.webClient = builder.baseUrl("https://gmail.googleapis.com/gmail/v1").build();
        this.tokenRefreshService = tokenRefreshService;
        this.userRepository = userRepository;
    }


    public Mono<List<String>> fetchLatestMails(User user, String clientId, String clientSecret) {

        System.out.println("user.getAccessToken() service :"+user.getAccessToken());
        System.out.println("user.getrefreshedToken() service :"+user.getRefreshToken());

        return getMessageIds(user.getAccessToken(), 5)
                .flatMapMany(Flux::fromIterable)
                .flatMap(id -> getEmailDetails(user.getAccessToken(), id))
                .collectList()
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
                                        getMessageIds(newAccessToken, 5)
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
                            .findFirst().map(h -> h.get("value")).orElse("Unknown")).replace("\\u003C", "<").replace("\\u003C", ">");;

                    return "  Labels: " + labelIds+
                             "| From: " + from +
                            " | Subject: " + subject
                            ;                });
    }



}