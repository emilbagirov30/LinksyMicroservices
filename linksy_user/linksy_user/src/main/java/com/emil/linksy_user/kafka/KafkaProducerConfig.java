package com.emil.linksy_user.kafka;

import com.emil.linksy_user.model.MediaRequest;
import com.emil.linksy_user.model.EmailRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${app.kafka.producer.max-request-size}")
    private int maxRequestSize;
    @Value("${app.kafka.producer.compression-type}")
    private String compressionType;
    private Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        return config;
    }

    @Bean
    public ProducerFactory<String, EmailRequest> producerEmailFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, EmailRequest> kafkaEmailTemplate() {
        return new KafkaTemplate<>(producerEmailFactory());
    }

    @Bean
    public ProducerFactory<String, MediaRequest> producerAvatarFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MediaRequest> kafkaAvatarTemplate() {
        return new KafkaTemplate<>(producerAvatarFactory());
    }
}
