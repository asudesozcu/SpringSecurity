package com.gmailsystem.consumerfeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableFeignClients(basePackages = "com.gmailsystem.consumerfeign.client")
@ComponentScan(basePackages = {"com.gmailsystem.consumerfeign", "com.gmailsystem.logging"})
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}