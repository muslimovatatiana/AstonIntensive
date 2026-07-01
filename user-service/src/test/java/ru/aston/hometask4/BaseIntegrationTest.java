package ru.aston.hometask4;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "eureka.client.enabled=false",

                "app.services.notification-url=http://localhost:8082",

                "app.notification.templates.create.subject=Test Create Subject",
                "app.notification.templates.create.text=Test Create Text %s",
                "app.notification.templates.delete.subject=Test Delete Subject",
                "app.notification.templates.delete.text=Test Delete Text",
                "app.notification.variables.site-name=test-site",

                "spring.liquibase.liquibase-schema=public",
                "springdoc.api-docs.enabled=false",
                "springdoc.swagger-ui.enabled=false"
        }
)
@AutoConfigureMockMvc
@Testcontainers
@EmbeddedKafka(
        partitions = 1,
        topics = {"user-events-topic"},
        controlledShutdown = true
)
public abstract class BaseIntegrationTest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");
}
