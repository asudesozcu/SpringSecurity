package com.example.googlelogin.config;


import com.example.googlelogin.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customoAuth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(CustomOAuth2UserService customoAuth2UserService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customoAuth2UserService = customoAuth2UserService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("in the filter chain");

        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/emails").authenticated()
                        .requestMatchers("api/admin/**").hasRole("ADMIN")
                        .requestMatchers("api/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customoAuth2UserService)
                        )
                        .successHandler(customAuthenticationSuccessHandler)
                )

                .csrf(csrf -> csrf.disable()); // CSRF disabled for token-based auth

        return http.build();
    }

}