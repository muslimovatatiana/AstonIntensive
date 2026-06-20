package ru.aston.hometask4.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.OutboxStatus;
import ru.aston.hometask4.repositories.OutboxEventRepository;
import ru.aston.hometask4.services.OutboxProcessorService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventScheduler {

    private static final String LOG_FOUND_EVENTS = "Found {} new outbox events to process.";

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxProcessorService outboxProcessorService;

    @Scheduled(fixedDelayString = "${app.scheduler.outbox-delay:30000}")
    public void processOutboxEvents() {
        List<OutboxEvent> newEvents = outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        if (newEvents.isEmpty()) {
            return;
        }

        log.info(LOG_FOUND_EVENTS, newEvents.size());

        for (OutboxEvent event : newEvents) {
            outboxProcessorService.processSingleEvent(event);
        }
    }
}
