package com.example.googlelogin.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Configuration
@EnableCaching
public class RedisConfig {
//THROWS ERROR


    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();

        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .value(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

}

