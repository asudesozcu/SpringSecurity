package com.gmailsystem.googlelogin.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/admin")
@RestController
public class AdminController {

    @GetMapping("/me")
    public Map<String, Object> currentAdmin(@AuthenticationPrincipal OAuth2User principal) {

        return Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email")
        );
    }
}
