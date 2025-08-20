package com.gmailsystem.googlelogin.service;

import com.gmailsystem.googlelogin.repo.UserRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class TokenRefreshService {
    private final WebClient webClient = WebClient.create("https://oauth2.googleapis.com");
    private final UserRepository userRepository;

    public TokenRefreshService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Map<String, String> getTokensFromAuthCode(String clientId, String clientSecret, String redirectUri, String authCode) {

        Map<String, String> tokenMap = webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", authCode)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        String accessToken = tokenMap.get("access_token");
        String refreshToken = tokenMap.get("refresh_token");

        String userEmail = webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .map(resp -> resp.get("email"))
                .block();

        if (userEmail != null) {
            userRepository.findByEmail(userEmail).ifPresent(user -> {
                user.setAccessToken(accessToken);
                if (refreshToken != null) {  // may be null on re-login
                    user.setRefreshToken(refreshToken);
                }
                userRepository.save(user);

                System.out.println("Tokens saved for user: " + userEmail);
            });
        }

        return tokenMap;
    }

    public Map<String, String> refreshAccessToken(String clientId, String clientSecret, String refreshToken) {
        System.out.println("DEBUG: refresh accesss token in token service");
        return webClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("refresh_token", refreshToken)
                        .with("grant_type", "refresh_token"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block();


    }


}
