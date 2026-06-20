package ru.aston.hometask5.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import ru.aston.hometask5.config.MailTemplatesProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String TEST_FROM_ADDRESS = "no-reply@astonintensive.ru";
    private static final String TEST_SITE_NAME = "ваш сайт";
    private static final String TEST_CREATE_SUBJECT = "Аккаунт создан!";
    private static final String TEST_CREATE_TEXT_TEMPLATE = "Здравствуйте! Ваш аккаунт на сайте %s был успешно создан.";
    private static final String TEST_DELETE_SUBJECT = "Аккаунт удален!";
    private static final String TEST_DELETE_TEXT = "Здравствуйте! Ваш аккаунт был удалён.";

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailTemplatesProperties mailProperties;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromAddress", TEST_FROM_ADDRESS);
    }

    @ParameterizedTest
    @CsvSource({"CREATE", "create"})
    void shouldSendCorrectNotificationForCreateAction(String action) {
        MailTemplatesProperties.Template createTemplate = new MailTemplatesProperties.Template(TEST_CREATE_SUBJECT, TEST_CREATE_TEXT_TEMPLATE);
        MailTemplatesProperties.Templates templates = new MailTemplatesProperties.Templates(createTemplate, null);
        MailTemplatesProperties.Variables variables = new MailTemplatesProperties.Variables(TEST_SITE_NAME);

        when(mailProperties.templates()).thenReturn(templates);
        when(mailProperties.variables()).thenReturn(variables);

        emailService.sendNotification(action, "user@test.com");

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();

        assertThat(capturedMessage.getTo()).containsExactly("user@test.com");
        assertThat(capturedMessage.getFrom()).isEqualTo(TEST_FROM_ADDRESS);
        assertThat(capturedMessage.getSubject()).isEqualTo(TEST_CREATE_SUBJECT);
        assertThat(capturedMessage.getText()).isEqualTo(String.format(TEST_CREATE_TEXT_TEMPLATE, TEST_SITE_NAME));
    }

    @ParameterizedTest
    @CsvSource({"DELETE", "delete"})
    void shouldSendCorrectNotificationForDeleteAction(String action) {
        MailTemplatesProperties.Template deleteTemplate = new MailTemplatesProperties.Template(TEST_DELETE_SUBJECT, TEST_DELETE_TEXT);
        MailTemplatesProperties.Templates templates = new MailTemplatesProperties.Templates(null, deleteTemplate);

        when(mailProperties.templates()).thenReturn(templates);

        emailService.sendNotification(action, "delete@test.com");

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();

        assertThat(capturedMessage.getTo()).containsExactly("delete@test.com");
        assertThat(capturedMessage.getFrom()).isEqualTo(TEST_FROM_ADDRESS);
        assertThat(capturedMessage.getSubject()).isEqualTo(TEST_DELETE_SUBJECT);
        assertThat(capturedMessage.getText()).isEqualTo(TEST_DELETE_TEXT);
    }

    @Test
    void shouldNotSendEmailWhenActionIsUnknown() {
        emailService.sendNotification("UNKNOWN_ACTION", "test@test.com");
        verifyNoInteractions(mailSender);
    }

    @ParameterizedTest
    @CsvSource({
            "direct1@test.com, Важное оповещение, Текст произвольного уведомления 1",
            "direct2@test.com, Системный сбой, Текст произвольного уведомления 2"
    })
    void shouldSendDirectSimpleMessage(String to, String subject, String text) {
        emailService.sendSimpleMessage(to, subject, text);

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage capturedMessage = mailMessageCaptor.getValue();

        assertThat(capturedMessage.getTo()).containsExactly(to);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getText()).isEqualTo(text);
        assertThat(capturedMessage.getFrom()).isEqualTo(TEST_FROM_ADDRESS);
    }

    @Test
    void shouldLogAndNotThrowExceptionWhenMailSenderFailsInNotification() {
        MailTemplatesProperties.Template createTemplate = new MailTemplatesProperties.Template(TEST_CREATE_SUBJECT, TEST_CREATE_TEXT_TEMPLATE);
        MailTemplatesProperties.Templates templates = new MailTemplatesProperties.Templates(createTemplate, null);
        MailTemplatesProperties.Variables variables = new MailTemplatesProperties.Variables(TEST_SITE_NAME);

        when(mailProperties.templates()).thenReturn(templates);
        when(mailProperties.variables()).thenReturn(variables);

        doThrow(new MailSendException("SMTP Server down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() ->
                emailService.sendNotification("CREATE", "test@test.com")
        );

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenMailSenderFailsInSimpleMessage() {
        doThrow(new MailSendException("Connection refused"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() ->
                emailService.sendSimpleMessage("test@test.com", "Тема", "Текст")
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mail server error");
    }

    @Test
    void shouldNotSendEmailWhenActionIsNull() {
        assertDoesNotThrow(() ->
                emailService.sendNotification(null, "test@test.com")
        );
        verifyNoInteractions(mailSender);
    }
}
