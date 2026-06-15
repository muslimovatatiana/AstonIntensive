package ru.aston.hometask4.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.hometask4.events.UserEvent;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.OutboxStatus;
import ru.aston.hometask4.repositories.OutboxEventRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventScheduler {

    private static final String LOG_FOUND_EVENTS = "Found {} new outbox events to process.";
    private static final String LOG_SUCCESS_PUBLISHED = "Event ID {} successfully published to Kafka topic.";
    private static final String LOG_CRITICAL_ERROR = "Critical error sending Event ID {} to Kafka. Marking as FAILED.";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    @Lazy
    private UserEventScheduler self;

    @Value("${spring.kafka.topic.user-events:user-events-topic}")
    private String topicName;

    @Scheduled(fixedDelayString = "${app.scheduler.outbox-delay:30000}")
    public void processOutboxEvents() {
        List<OutboxEvent> newEvents = outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        if (newEvents.isEmpty()) {
            return;
        }

        log.info(LOG_FOUND_EVENTS, newEvents.size());

        for (OutboxEvent event : newEvents) {
            try {
                String actionStr = event.getAction().name();
                UserEvent kafkaPayload = new UserEvent(actionStr, event.getEmail());

                kafkaTemplate.send(topicName, event.getEmail(), kafkaPayload).join();

                self.updateEventStatus(event, OutboxStatus.PROCESSED);
                log.info(LOG_SUCCESS_PUBLISHED, event.getId());
            } catch (Exception e) {
                log.error(LOG_CRITICAL_ERROR, event.getId(), e);
                self.updateEventStatus(event, OutboxStatus.FAILED);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEventStatus(OutboxEvent event, OutboxStatus targetStatus) {
        event.setStatus(targetStatus);
        outboxEventRepository.save(event);
    }
}
