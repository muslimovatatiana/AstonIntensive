package ru.aston.hometask4.dto;

import jakarta.validation.constraints.*;

public record UserRequestDto(
        @NotBlank(message = "{user.name.not_blank}")
        String name,

        @NotBlank(message = "{user.email.not_blank}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotNull(message = "{user.age.not_null}")
        @Min(value = 0, message = "user.age.min")
        @Max(value = 150, message = "user.age.max")
        Integer age
) {}
