package com.gmailsystem.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.gmailsystem.graphql")
@EnableFeignClients(basePackages = {
        "com.gmailsystem.graphql",
})
@EnableKafka
@ComponentScan(basePackages = {"com.gmailsystem.graphql", "com.gmailsystem.logging"})

public class GraphqlLayerApplication {
    public static void main(String[] args) {

        SpringApplication.run(GraphqlLayerApplication.class, args);

    }
}