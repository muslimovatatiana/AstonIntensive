package ru.aston.hometask5.events;

public record UserEvent(
        String action,
        String email
) {}
