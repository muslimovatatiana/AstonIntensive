package ru.aston.hometask4.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.hometask4.events.UserEvent;
import ru.aston.hometask4.kafka.UserEventProducer;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.OutboxStatus;
import ru.aston.hometask4.repositories.OutboxEventRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxProcessorService {

    private static final String LOG_SUCCESS_PUBLISHED = "Event ID {} successfully published to Kafka topic.";
    private static final String LOG_CRITICAL_ERROR = "Critical error sending Event ID {} to Kafka. Marking as FAILED.";

    private final OutboxEventRepository outboxEventRepository;
    private final UserEventProducer userEventProducer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleEvent(OutboxEvent event) {
        try {
            String actionStr = event.getAction().name();
            UserEvent kafkaPayload = new UserEvent(actionStr, event.getEmail());

            userEventProducer.send(event.getEmail(), kafkaPayload);

            updateEventStatus(event, OutboxStatus.PROCESSED);
            log.info(LOG_SUCCESS_PUBLISHED, event.getId());
        } catch (Exception e) {
            log.error(LOG_CRITICAL_ERROR, event.getId(), e);
            updateEventStatus(event, OutboxStatus.FAILED);
        }
    }

    private void updateEventStatus(OutboxEvent event, OutboxStatus targetStatus) {
        event.setStatus(targetStatus);
        outboxEventRepository.save(event);
    }
}
