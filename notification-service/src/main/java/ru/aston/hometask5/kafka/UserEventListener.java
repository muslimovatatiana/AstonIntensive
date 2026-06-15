package ru.aston.hometask5.kafka;

import ru.aston.hometask5.events.UserEvent;
import ru.aston.hometask5.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {
    private static final String LOG_RECEIVED_EVENT = "Received event from Kafka: action={}, email={}";

    private final EmailService emailService;

    @KafkaListener(
            topics = "${spring.kafka.topic.user-events:user-events-topic}",
            groupId = "notification-group"
    )

    public void listenUserEvents(UserEvent event) {
        log.info(LOG_RECEIVED_EVENT, event.action(), event.email());
        emailService.sendNotification(event.action(), event.email());
    }
}
