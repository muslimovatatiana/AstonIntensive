package ru.aston.hometask4.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
