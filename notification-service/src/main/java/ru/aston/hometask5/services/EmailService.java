package ru.aston.hometask5.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.hometask5.config.MailTemplatesProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String LOG_INVALID_DATA = "Skipped email sending: invalid input data action={}, email={}";
    private static final String LOG_SUCCESS_NOTIFICATION = "Email successfully sent to {} for action: {}";
    private static final String LOG_SMTP_ERROR = "Failed to send email via SMTP server for {}: {}";
    private static final String LOG_REST_SUCCESS = "Direct REST email successfully sent to {}";
    private static final String LOG_REST_ERROR = "Error sending REST email to {}: {}";
    private static final String LOG_UNKNOWN_ACTION = "Unknown action received from Kafka: {}. Sending canceled.";
    private static final String EXCEPTION_MAIL_SERVER_ERROR = "Mail server error: ";

    private static final String ACTION_CREATE = "CREATE";
    private static final String ACTION_DELETE = "DELETE";

    private final JavaMailSender mailSender;
    private final MailTemplatesProperties mailProperties;

    @Value("${spring.mail.from-address}")
    private String fromAddress;

    public void sendNotification(String action, String email) {
        if (action == null || email == null || email.trim().isEmpty()) {
            log.warn(LOG_INVALID_DATA, action, email);
            return;
        }

        try {
            SimpleMailMessage message = buildNotificationMessage(action.toUpperCase(), email);
            if (message != null) {
                mailSender.send(message);
                log.info(LOG_SUCCESS_NOTIFICATION, email, action);
            }
        } catch (MailSendException e) {
            log.error(LOG_SMTP_ERROR, email, e.getMessage());
        }
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info(LOG_REST_SUCCESS, to);
        } catch (MailSendException e) {
            log.error(LOG_REST_ERROR, to, e.getMessage());
            throw new RuntimeException(EXCEPTION_MAIL_SERVER_ERROR + e.getMessage(), e);
        }
    }

    private SimpleMailMessage buildNotificationMessage(String action, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);

        switch (action) {
            case ACTION_CREATE -> {
                String siteName = mailProperties.variables().siteName();

                MailTemplatesProperties.Template createTpl = mailProperties.templates().create();
                message.setSubject(createTpl.subject());
                message.setText(String.format(createTpl.text(), siteName));
            }
            case ACTION_DELETE -> {
                MailTemplatesProperties.Template deleteTpl = mailProperties.templates().delete();
                message.setSubject(deleteTpl.subject());
                message.setText(deleteTpl.text());
            }
            default -> {
                log.warn(LOG_UNKNOWN_ACTION, action);
                return null;
            }
        }
        return message;
    }

}
