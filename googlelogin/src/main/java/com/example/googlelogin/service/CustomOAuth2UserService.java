package com.example.googlelogin.service;

import com.example.googlelogin.model.User;
import com.example.googlelogin.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);


    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("loaduser called");

        log.info("Google attributes: " + attributes.toString());


        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null || name == null) {
            throw new RuntimeException("Google'dan gerekli bilgiler alınamadı: email veya name null.");
        }

        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setName(name);
        user.setRole("USER");
        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub"
        );
    }

}
