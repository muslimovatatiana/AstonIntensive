package ru.aston.hometask2;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DatabaseConnectionTest {

    @Test
    public void shouldConnectToDatabase() {
        try (SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory()) {
            assertNotNull(sessionFactory, "Фабрика сессий не должна быть null");
            assertFalse(sessionFactory.isClosed(), "Фабрика сессий должна быть открыта");
        }
    }
}
