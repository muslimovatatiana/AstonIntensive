package ru.aston.hometask4.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "NOTIFICATION-SERVICE")
interface FeignNotificationApi {
    @PostMapping("/api/v1/notifications")
    void sendDirectNotification(@RequestBody Map<String, String> requestBody);
}

