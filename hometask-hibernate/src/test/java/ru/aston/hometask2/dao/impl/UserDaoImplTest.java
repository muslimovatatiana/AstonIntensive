package ru.aston.hometask2.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.hometask2.dao.BaseIntegrationTest;
import ru.aston.hometask2.exception.impl.UserNotFoundException;
import ru.aston.hometask2.models.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoImplTest extends BaseIntegrationTest {

    private UserDaoImpl userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(testSessionFactory);
    }

    @Test
    void save_ShouldPersistUser_WhenValidData() {
        User user = User.builder()
                .name("ФИО")
                .email("new_save@mail.ru")
                .age(25)
                .build();
        Long generatedId = userDao.save(user);
        assertNotNull(generatedId);
        User savedUser = userDao.findById(generatedId).orElse(null);

        assertNotNull(savedUser);
        assertEquals("new_save@mail.ru", savedUser.getEmail());
    }

    @Test
    void save_ShouldThrowRuntimeException_WhenEmailAlreadyExists() {
        User user1 = User.builder()
                .name("Первый")
                .email("same_email@mail.ru")
                .age(30)
                .build();
        userDao.save(user1);

        User user2 = User.builder()
                .name("Второй")
                .email("same_email@mail.ru")
                .age(25)
                .build();

        assertThrows(RuntimeException.class, () -> userDao.save(user2));
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User user = User.builder().name("Роуз").email("rose@mail.ru").age(22).build();
        Long id = userDao.save(user);

        Optional<User> foundUser = userDao.findById(id);

        assertTrue(foundUser.isPresent());
        assertEquals("rose@mail.ru", foundUser.get().getEmail());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> foundUser = userDao.findById(999999L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllPersistedUsers() {
        assertTrue(userDao.findAll().isEmpty());
        User user1 = User.builder().name("Пользователь 1").email("user1@mail.ru").age(20).build();
        User user2 = User.builder().name("Пользователь 2").email("user2@mail.ru").age(30).build();
        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void update_ShouldModifyFields_WhenUserExists() {
        User user = User.builder().name("Марк").email("mark@mail.ru").age(40).build();
        Long id = userDao.save(user);
        User userToUpdate = userDao.findById(id).orElseThrow();
        userToUpdate.setName("Марк обновлен");
        userToUpdate.setEmail("new_mark@mail.ru");
        userToUpdate.setAge(50);
        userDao.update(userToUpdate);
        User updatedUser = userDao.findById(id).orElseThrow();

        assertEquals("Марк обновлен", updatedUser.getName());
        assertEquals("new_mark@mail.ru", updatedUser.getEmail());
        assertEquals(50, updatedUser.getAge());
    }

    @Test
    void update_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        User nonExistingUser = User.builder().id(999999L).name("NotFound").email("not_found@mail.ru").age(30).build();

        assertThrows(UserNotFoundException.class, () -> userDao.update(nonExistingUser));
    }

    @Test
    void update_ShouldThrowRuntimeException_WhenTargetEmailAlreadyExists() {
        User user1 = User.builder().name("Иван").email("ivan@mail.ru").age(30).build();
        User user2 = User.builder().name("Петр").email("petr@mail.ru").age(25).build();

        Long id1 = userDao.save(user1);
        Long id2 = userDao.save(user2);

        User userToUpdate = userDao.findById(id2).orElseThrow();
        userToUpdate.setEmail("ivan@mail.ru");

        assertThrows(RuntimeException.class, () -> userDao.update(userToUpdate));
    }

    @Test
    void deleteById_ShouldRemoveUser_WhenUserExists() {
        User user = User.builder().name("Лиза").email("liza@mail.ru").age(19).build();
        Long id = userDao.save(user);
        userDao.deleteById(id);
        Optional<User> deletedUser = userDao.findById(id);

        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void deleteById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        assertThrows(UserNotFoundException.class, () -> userDao.deleteById(999999L));
    }

}
