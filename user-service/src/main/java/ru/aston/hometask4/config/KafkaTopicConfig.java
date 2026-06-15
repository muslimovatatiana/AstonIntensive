package ru.aston.hometask4.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.user-events:user-events-topic}")
    private String topicName;

    @Value("${spring.kafka.topic.partitions:1}")
    private int partitions;

    @Value("${spring.kafka.topic.replication-factor:1}")
    private int replicationFactor;

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .build();
    }
}
