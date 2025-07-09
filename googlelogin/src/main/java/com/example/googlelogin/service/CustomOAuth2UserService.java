package com.example.googlelogin.service;

import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@Primary
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public CustomOAuth2UserService(UserRepository userRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this.userRepository = userRepository;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2AccessToken accessToken = request.getAccessToken();
        System.out.println(" Access Token in loadUser: " + accessToken.getTokenValue());
        System.out.println(" Access Token exp in loadUser: " + accessToken.getExpiresAt());


        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        System.out.println("Google attributes: " + attributes);

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email).orElse(null);

        user.setAccessToken(accessToken.getTokenValue());

        if (user == null) {
            System.out.println("user not in db");
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setRole("USER");
            userRepository.save(user);
        }

        String role=user.getRole();
        System.out.println("ROLE:"  + role);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_"+role)),
                attributes,
                "sub"
        );
    }

}
