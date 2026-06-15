package ru.aston.hometask4.events;

public record UserEvent(
        String action,
        String email
) {}
