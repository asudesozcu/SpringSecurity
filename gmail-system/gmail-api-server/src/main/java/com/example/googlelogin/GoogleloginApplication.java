package com.example.googlelogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.googlelogin.repo")
@EnableCaching

public class GoogleloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleloginApplication.class, args);
	}

}
