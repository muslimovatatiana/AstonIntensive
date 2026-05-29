package ru.aston.hometask2.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.hometask2.dao.UserDao;
import ru.aston.hometask2.exception.impl.UserNotFoundException;
import ru.aston.hometask2.models.User;
import ru.aston.hometask2.util.HibernateUtil;
import java.util.List;
import java.util.Optional;

import static ru.aston.hometask2.util.AppMessages.ERROR_DAO_FIND_ALL;
import static ru.aston.hometask2.util.AppMessages.ERROR_DAO_SAVE;
import static ru.aston.hometask2.util.AppMessages.getErrorDaoDelete;
import static ru.aston.hometask2.util.AppMessages.getErrorDaoFindById;
import static ru.aston.hometask2.util.AppMessages.getErrorDaoUpdate;
import static ru.aston.hometask2.util.AppMessages.getErrorDaoUserNotFound;

public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public Long save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user.getId();
        } catch (Exception e) {
            log.error(ERROR_DAO_SAVE, e);
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            throw new RuntimeException(ERROR_DAO_SAVE, e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        } catch (Exception e) {
            log.error(getErrorDaoFindById(id), e);
            throw new RuntimeException(getErrorDaoFindById(id), e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var cb = session.getCriteriaBuilder();
            var cq = cb.createQuery(User.class);
            cq.from(User.class);
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            log.error(ERROR_DAO_FIND_ALL, e);
            throw new RuntimeException(ERROR_DAO_FIND_ALL, e);
        }
    }

    @Override
    public void update(User user) {
        Long id = user.getId();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            int updatedRows = session.createMutationQuery(
                            "update User set name = :name, email = :email, age = :age where id = :id")
                    .setParameter("name", user.getName())
                    .setParameter("email", user.getEmail())
                    .setParameter("age", user.getAge())
                    .setParameter("id", id)
                    .executeUpdate();

            if (updatedRows == 0) {
                throw new UserNotFoundException(getErrorDaoUserNotFound(id));
            }

            transaction.commit();
        } catch (UserNotFoundException e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                try {
                    transaction.rollback();
                } catch (Exception ignored) {
                }
            }
            throw e;
        } catch (Exception e) {
            log.error(getErrorDaoUpdate(id), e);
            if (transaction != null && transaction.isActive() && transaction.getStatus().canRollback()) {
                try {
                    transaction.rollback();
                } catch (Exception ignored) {
                }
            }
            throw new RuntimeException(getErrorDaoUpdate(id), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            int deletedRows = session.createMutationQuery("delete from User where id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            if (deletedRows == 0) {
                throw new UserNotFoundException(getErrorDaoUserNotFound(id));
            }

            transaction.commit();
        } catch (UserNotFoundException e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                try { transaction.rollback(); } catch (Exception ignored) {}
            }
            throw e;
        } catch (Exception e) {
            log.error(getErrorDaoDelete(id), e);
            if (transaction != null && transaction.isActive() && transaction.getStatus().canRollback()) {
                try { transaction.rollback(); } catch (Exception ignored) {}
            }
            throw new RuntimeException(getErrorDaoDelete(id), e);
        }
    }
}
