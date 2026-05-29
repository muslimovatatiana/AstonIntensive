package ru.aston.hometask2.dao;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.hometask2.util.HibernateUtil;

import java.util.Properties;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_user_service_db")
            .withUsername("test_postgres")
            .withPassword("test_postgres");

    protected static SessionFactory testSessionFactory;

    @BeforeAll
    static void startContainer() {
        postgres.start();

        Properties testProperties = new Properties();
        testProperties.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        testProperties.setProperty("hibernate.connection.username", postgres.getUsername());
        testProperties.setProperty("hibernate.connection.password", postgres.getPassword());
        testProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        testSessionFactory = HibernateUtil.getTestSessionFactory(testProperties);
    }
}
