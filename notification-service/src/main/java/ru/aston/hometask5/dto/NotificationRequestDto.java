package ru.aston.hometask5.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NotificationRequestDto(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email must contain a dot and a top-level domain")
        String email,

        @NotBlank(message = "Subject must not be blank")
        @Size(max = 512, message = "Subject must not exceed 512 characters")
        String subject,

        @NotBlank(message = "Message body must not be blank")
        @Size(max = 5000, message = "Message body must not exceed 5000 characters")
        String message
) {}
