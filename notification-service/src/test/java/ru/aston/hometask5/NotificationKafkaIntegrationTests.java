package ru.aston.hometask5;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.aston.hometask5.config.KafkaTestConfig;
import ru.aston.hometask5.events.UserEvent;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(topics = "user-events-topic")
@Import(KafkaTestConfig.class)
@ActiveProfiles("test")
class NotificationKafkaIntegrationTests {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
            .withPerMethodLifecycle(true);

    @Autowired
    private KafkaTemplate<String, Object> testKafkaTemplate;

    @ParameterizedTest(name = "Обработка события [{0}] -> {1}")
    @CsvSource({
            "CREATE, kafka-create@example.com, Здравствуйте! Ваш аккаунт на сайте your-site был успешно создан.",
            "DELETE, kafka-delete@example.com, Здравствуйте! Ваш аккаунт был удалён."
    })
    void shouldProcessKafkaEventAndSendEmail(String action, String email, String expectedBodyText) throws Exception {
        greenMail.purgeEmailFromAllMailboxes();

        UserEvent userEvent = new UserEvent(action, email);
        testKafkaTemplate.send("user-events-topic", userEvent).get(5, TimeUnit.SECONDS);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).hasSize(1);
            assertThat(receivedMessages[0].getAllRecipients()[0].toString()).isEqualTo(email);

            String mailContent = receivedMessages[0].getContent().toString().trim();
            assertThat(mailContent).contains(expectedBodyText);
        });
    }

    @Test
    void shouldNotSendEmailWhenEventEmailIsEmpty() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
        UserEvent invalidEvent = new UserEvent("CREATE", "");

        testKafkaTemplate.send("user-events-topic", invalidEvent).get(5, TimeUnit.SECONDS);

        await().during(2, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(greenMail.getReceivedMessages()).isEmpty()
        );
    }

    @Test
    void shouldHandlePoisonPillMessageWithoutCrashing() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();

        testKafkaTemplate.send("user-events-topic", "{\"invalid_json\": broken...").get(5, TimeUnit.SECONDS);

        await().during(2, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(greenMail.getReceivedMessages()).isEmpty()
        );
    }
}
