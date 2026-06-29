package ru.aston.hometask4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "${swagger.user.response.schema}")
public record UserResponseDto(
        @Schema(description = "${swagger.user.id.desc}", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "${swagger.user.name.desc}", example = "Иван")
        String name,

        @Schema(description = "${swagger.user.email.desc}", example = "ivan@example.com")
        String email,

        @Schema(description = "${swagger.user.age.desc}", example = "25")
        Integer age,

        @Schema(description = "${swagger.user.createdAt.desc}")
        LocalDateTime createdAt
) {}
