--liquibase formatted sql

--changeset muslimova_t:1-create-schema-and-table-with-uuid
CREATE SCHEMA IF NOT EXISTS spring;
SET search_path TO spring;

CREATE TABLE users (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT check_user_age CHECK (age >= 0 AND age <= 150),
    CONSTRAINT check_user_name CHECK (length(trim(name)) > 0),
    CONSTRAINT check_user_email CHECK (email LIKE '%_@__%.__%')
);
