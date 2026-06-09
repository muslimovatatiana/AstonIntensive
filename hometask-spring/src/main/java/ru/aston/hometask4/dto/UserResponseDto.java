package ru.aston.hometask4.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        Integer age,
        LocalDateTime createdAt
) {}
