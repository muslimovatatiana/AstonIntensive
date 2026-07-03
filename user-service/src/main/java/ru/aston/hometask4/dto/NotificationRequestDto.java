package ru.aston.hometask4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import ru.aston.hometask4.models.UserAction;

public record NotificationRequestDto(
        @NotNull(message = "{user.action.not_null}")
        UserAction action,

        @Email(message = "{user.email.invalid}")
        @NotNull(message = "{user.email.not_blank}")
        String email
) {}
