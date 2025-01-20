package com.emil.linksy_cloud.kafka;



import com.emil.linksy_cloud.model.*;
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

    @Bean
    public ProducerFactory<String, MessageKafkaResponse> producerMessageFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MessageKafkaResponse> kafkaMessageTemplate() {
        return new KafkaTemplate<>(producerMessageFactory());

    }


    @Bean
    public ProducerFactory<String, GroupKafkaResponse> producerGroupFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, GroupKafkaResponse> kafkaGroupTemplate() {
        return new KafkaTemplate<>(producerGroupFactory());

    }
    @Bean
    public ProducerFactory<String, ChannelKafkaResponse> producerChannelFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, ChannelKafkaResponse> kafkaChannelTemplate() {
        return new KafkaTemplate<>(producerChannelFactory());

    }


    @Bean
    public ProducerFactory<String,ChannelPostKafkaResponse> producerChannelPostFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, ChannelPostKafkaResponse> kafkaChannelPostTemplate() {
        return new KafkaTemplate<>(producerChannelPostFactory());

    }

    @Bean
    public ProducerFactory<String,GroupEditDataKafkaResponse> producerCGroupEditFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, GroupEditDataKafkaResponse> kafkaGroupEditTemplate() {
        return new KafkaTemplate<>(producerCGroupEditFactory());

    }
}
