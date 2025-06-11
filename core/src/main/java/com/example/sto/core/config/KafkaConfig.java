package com.example.sto.core.config;

import com.example.sto.core.kafka.StatusChangeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${sto.kafka.topic.request-status}")
    private String statusTopic;

    @Bean
    public NewTopic statusTopic() {
        return TopicBuilder.name(statusTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ProducerFactory<String, StatusChangeEvent> producerFactory(Environment env) {
        return new DefaultKafkaProducerFactory<>(KafkaUtils.producerProps(env));
    }

    @Bean
    public KafkaTemplate<String, StatusChangeEvent> kafkaTemplate(
            ProducerFactory<String, StatusChangeEvent> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ConsumerFactory<String, StatusChangeEvent> consumerFactory(Environment env) {
        return new DefaultKafkaConsumerFactory<>(KafkaUtils.consumerProps(env, String.class, StatusChangeEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StatusChangeEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, StatusChangeEvent> cf) {
        ConcurrentKafkaListenerContainerFactory<String, StatusChangeEvent> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(cf);
        return f;
    }
}
