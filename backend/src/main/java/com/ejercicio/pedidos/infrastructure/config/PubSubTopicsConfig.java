package com.ejercicio.pedidos.infrastructure.config;

import com.google.cloud.spring.pubsub.PubSubAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubTopicsConfig {

    @Bean
    ApplicationRunner crearTopics(
            PubSubAdmin admin,
            @Value("${pedidos.pubsub.topic-estado-cambiado}") String topic) {
        return args -> {
            if (admin.getTopic(topic) == null) {
                admin.createTopic(topic);
            }
        };
    }
}