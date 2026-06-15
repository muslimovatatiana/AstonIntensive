--liquibase formatted sql

--changeset muslimova_t:2-create-outbox-events-table
CREATE TABLE spring.outbox_events (
    id UUID PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_outbox_events_status_created ON spring.outbox_events (status, created_at);
