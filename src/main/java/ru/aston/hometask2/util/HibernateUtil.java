package ru.aston.hometask2.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static ru.aston.hometask2.util.AppMessages.ERROR_SESSION_FACTORY_CREATION;
import static ru.aston.hometask2.util.AppMessages.ERROR_TEST_SESSION_FACTORY_CREATION;

public final class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory testSessionFactory;

    private HibernateUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            log.error(ERROR_SESSION_FACTORY_CREATION, ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    public static SessionFactory getTestSessionFactory(Properties testProperties) {
        if (testSessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addProperties(testProperties);
                testSessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                log.error(ERROR_TEST_SESSION_FACTORY_CREATION, ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
        return testSessionFactory;
    }
}
