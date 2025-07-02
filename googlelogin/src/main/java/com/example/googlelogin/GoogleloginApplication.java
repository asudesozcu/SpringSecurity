package com.example.googlelogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.googlelogin.repo")

public class GoogleloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleloginApplication.class, args);
	}

}
