package com.emil.linksy_user.kafka;

import com.emil.linksy_user.model.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, MediaResponse> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>( MediaResponse.class, false)));
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MediaResponse> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MediaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<String, PostKafkaResponse> postKafkaResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id_post");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(PostKafkaResponse.class, false)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostKafkaResponse> postKafkaResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostKafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(postKafkaResponseConsumerFactory());
        return factory;
    }



    @Bean
    public ConsumerFactory<String, MomentKafkaResponse>momentKafkaResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id_moment");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(MomentKafkaResponse.class, false)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MomentKafkaResponse> momentKafkaResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MomentKafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(momentKafkaResponseConsumerFactory());
        return factory;
    }





    @Bean
    public ConsumerFactory<String, MessageKafkaResponse> messageKafkaResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id_message");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(MessageKafkaResponse.class, false)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageKafkaResponse> messageKafkaResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageKafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(messageKafkaResponseConsumerFactory());
        return factory;
    }





    @Bean
    public ConsumerFactory<String, GroupKafkaResponse> groupKafkaResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id_group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(GroupKafkaResponse.class, false)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroupKafkaResponse> groupKafkaResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GroupKafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(groupKafkaResponseConsumerFactory());
        return factory;
    }



    @Bean
    public ConsumerFactory<String, ChannelKafkaResponse> channelKafkaResponseConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id_channel");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(ChannelKafkaResponse.class, false)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChannelKafkaResponse> channelKafkaResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChannelKafkaResponse> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(channelKafkaResponseConsumerFactory());
        return factory;
    }
}