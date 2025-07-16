package kafka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Arrays;

@SpringBootApplication
@EnableKafka
public class KafkaConsumerApplication {
    @Bean
    public CommandLineRunner testRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("🧠 Beans loaded:");
            Arrays.stream(ctx.getBeanDefinitionNames())
                    .filter(name -> name.contains("emailConsumer"))
                    .forEach(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }
}
