package ru.aston.hometask5.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.aston.hometask5.dto.NotificationRequestDto;
import ru.aston.hometask5.services.EmailService;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final String LOG_SEND_EMAIL_REQUEST = "Received REST request to send email to: {}";

    private final EmailService emailService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendDirectNotification(@Valid @RequestBody NotificationRequestDto request) {
        log.info(LOG_SEND_EMAIL_REQUEST, request.email());
        emailService.sendSimpleMessage(request.email(), request.subject(), request.message());
    }
}
