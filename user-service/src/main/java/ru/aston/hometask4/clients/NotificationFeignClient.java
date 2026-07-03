package ru.aston.hometask4.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import ru.aston.hometask4.exceptions.InvalidNotificationActionException;
import ru.aston.hometask4.models.UserAction;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class NotificationFeignClient {

    private static final String LOG_FEIGN_CALL = "Calling OpenFeign to send sync email to: {}";
    private static final String LOG_CB_TRIGGERED = "=== CIRCUIT BREAKER TRIGGERED (RESILIENCE4J + FEIGN) ===";
    private static final String LOG_CB_ERROR = "Remote notification-service is down or failed. Error: {}";
    private static final String LOG_CB_ISOLATED = "Sync notification for {} failed, but the client isolated the fault.";
    private static final String INVALID_ACTION_KEY = "notification.invalid_action";

    private final FeignNotificationApi feignApi;

    @Value("${app.notification.templates.create.subject}")
    private String createSubject;

    @Value("${app.notification.templates.create.text}")
    private String createText;

    @Value("${app.notification.templates.delete.subject}")
    private String deleteSubject;

    @Value("${app.notification.templates.delete.text}")
    private String deleteText;

    @Value("${app.notification.variables.site-name:your-site}")
    private String siteName;

    @CircuitBreaker(name = "notificationServiceCB", fallbackMethod = "fallbackSendNotification")
    public void sendDirectNotification(UserAction action, String email) {
        log.info(LOG_FEIGN_CALL, email);

        feignApi.sendDirectNotification(switch (action) {
            case CREATE -> Map.of("email", email, "subject", createSubject, "message", String.format(createText, siteName));
            case DELETE -> Map.of("email", email, "subject", deleteSubject, "message", deleteText);
            default -> throw new InvalidNotificationActionException(INVALID_ACTION_KEY, action.name());
        });
    }

    public void fallbackSendNotification(UserAction action, String email, Throwable throwable) {
        log.error(LOG_CB_TRIGGERED);
        log.error(LOG_CB_ERROR, throwable.getMessage());
        log.error(LOG_CB_ISOLATED, email);
    }
}
