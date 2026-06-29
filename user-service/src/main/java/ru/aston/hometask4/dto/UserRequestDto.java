package ru.aston.hometask4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "${swagger.user.request.schema}")
public record UserRequestDto(
        @NotBlank(message = "{user.name.not_blank}")
        @Schema(description = "${swagger.user.name.desc}", example = "Иван", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @NotBlank(message = "{user.email.not_blank}")
        @Email(message = "{user.email.invalid}")
        @Schema(description = "${swagger.user.email.desc}", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotNull(message = "{user.age.not_null}")
        @Min(value = 0, message = "{user.age.min}")
        @Max(value = 150, message = "{user.age.max}")
        @Schema(description = "${swagger.user.age.desc}", example = "25", minimum = "0", maximum = "150", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer age
) {}
