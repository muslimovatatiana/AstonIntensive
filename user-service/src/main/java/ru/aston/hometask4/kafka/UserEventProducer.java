package ru.aston.hometask4.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.aston.hometask4.events.UserEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String LOG_ERROR_SEND = "Failed to send message to Kafka topic: {}";
    private static final String ERROR_KAFKA_SEND = "Kafka send error";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.user-events:user-events-topic}")
    private String topicName;

    public void send(String key, UserEvent payload) {
        try {
            kafkaTemplate.send(topicName, key, payload).join();
        } catch (Exception e) {
            log.error(LOG_ERROR_SEND, topicName, e);
            throw new RuntimeException(ERROR_KAFKA_SEND, e);
        }
    }
}
