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
