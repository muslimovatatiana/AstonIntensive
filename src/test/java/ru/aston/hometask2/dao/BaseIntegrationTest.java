package ru.aston.hometask2.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.hometask2.util.HibernateTestUtil;

import static ru.aston.hometask2.util.AppMessages.ERROR_CLEAN_TEST_DATABASE;

@Testcontainers
public abstract class BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(BaseIntegrationTest.class);

    protected static SessionFactory testSessionFactory;

    @BeforeAll
    static void startContainer() {
        testSessionFactory = HibernateTestUtil.getTestSessionFactory();
    }

    @BeforeEach
    void clearDatabase() {
        try (Session session = testSessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error(ERROR_CLEAN_TEST_DATABASE, e);
            throw new RuntimeException(ERROR_CLEAN_TEST_DATABASE, e);
        }
    }

    @AfterAll
    static void stopContainer() {
        HibernateTestUtil.shutdown();
    }
}
