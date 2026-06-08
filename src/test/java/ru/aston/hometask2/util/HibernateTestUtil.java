package ru.aston.hometask2.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.aston.hometask2.util.AppMessages.ERROR_TEST_SESSION_FACTORY_CREATION;

public final class HibernateTestUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateTestUtil.class);
    private static final String TEST_CONFIG_FILE = "hibernate-test.cfg.xml";
    private static SessionFactory testSessionFactory;

    private HibernateTestUtil() {}

    public static SessionFactory getTestSessionFactory() {
        if (testSessionFactory == null || testSessionFactory.isClosed()) {
            try {
                testSessionFactory = new Configuration()
                        .configure(TEST_CONFIG_FILE)
                        .buildSessionFactory();
            } catch (Throwable ex) {
                log.error(ERROR_TEST_SESSION_FACTORY_CREATION, ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return testSessionFactory;
    }

    public static void shutdown() {
        if (testSessionFactory != null && !testSessionFactory.isClosed()) {
            testSessionFactory.close();
            testSessionFactory = null;
        }
    }
}
