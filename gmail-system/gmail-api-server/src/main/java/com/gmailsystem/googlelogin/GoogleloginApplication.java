package com.gmailsystem.googlelogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.gmailsystem.googlelogin.repo")

@EnableCaching
@ComponentScan(basePackages = {

		"com.gmailsystem.googlelogin", // kendi ana paketini buraya yaz
		"com.gmailsystem.logging"                  // shared-library içindeki loglama paketini buraya ekle
})
//@EnableAspectJAutoProxy(proxyTargetClass = false, exposeProxy = true)

public class GoogleloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoogleloginApplication.class, args);
	}

}
