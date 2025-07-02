package com.example.googlelogin;


import com.example.googlelogin.repo.UserRepository;
import com.example.googlelogin.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customoAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customoAuth2UserService) {
        this.customoAuth2UserService = customoAuth2UserService;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customoAuth2UserService)
                        )

                        .defaultSuccessUrl("/api/user/me", true) // google login sonrası yönlendirme
                )
                .csrf(csrf -> csrf.disable()); // CSRF koruması, genelde form bazlı oturumlar için gereklidir.Ancak REST API geliştiriyorsan, CSRF kapatılır, çünkü token bazlı güvenlik kullanılır.

        return http.build();
    }

}
