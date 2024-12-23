package com.emil.linksy_cloud.kafka;



import com.emil.linksy_cloud.model.MediaResponse;
import com.emil.linksy_cloud.model.PostKafkaResponse;
import com.emil.linksy_cloud.model.MomentKafkaResponse;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
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

    private Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        return config;
    }

    @Bean
    public ProducerFactory<String, MediaResponse> producerMediaFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MediaResponse> kafkaMediaTemplate() {
        return new KafkaTemplate<>(producerMediaFactory());
    }


    @Bean
    public ProducerFactory<String, PostKafkaResponse> producerPostFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, PostKafkaResponse> kafkaPostTemplate() {
        return new KafkaTemplate<>(producerPostFactory());
    }

    @Bean
    public ProducerFactory<String, MomentKafkaResponse> producerMomentFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MomentKafkaResponse> kafkaMomentTemplate() {
        return new KafkaTemplate<>(producerMomentFactory());
    }


}
