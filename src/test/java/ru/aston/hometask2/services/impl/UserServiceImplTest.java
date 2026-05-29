package ru.aston.hometask2.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hometask2.dao.UserDao;
import ru.aston.hometask2.exception.impl.UserAlreadyExistsException;
import ru.aston.hometask2.exception.impl.UserNotFoundException;
import ru.aston.hometask2.exception.impl.UserValidationException;
import ru.aston.hometask2.models.User;
import ru.aston.hometask2.util.AppMessages;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.aston.hometask2.util.AppConstants.DB_UNIQUE_EMAIL_CONSTRAINT;
import static ru.aston.hometask2.util.AppConstants.MAX_USER_AGE;
import static ru.aston.hometask2.util.AppConstants.MIN_USER_AGE;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        Long nonExistingId = 99L;
        String expectedMessage = AppMessages.getErrorUserNotFound(nonExistingId);
        when(userDao.findById(nonExistingId)).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(nonExistingId));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest(name = "[{index}] Проверка на кейс: {0}")
    @MethodSource("provideInvalidUsers")
    void registerUser_ShouldThrowValidationException_WhenDataIsInvalid(String scenario, User invalidUser) {
        assertThrows(UserValidationException.class, () -> userService.registerUser(invalidUser));
    }

    private static Stream<Arguments> provideInvalidUsers() {
        return Stream.of(
                Arguments.of("User is null", null),
                Arguments.of("Name is empty", User.builder().name("").email("test@mail.ru").age(25).build()),
                Arguments.of("Name is with spaces", User.builder().name("   ").email("test@mail.ru").age(25).build()),
                Arguments.of("Email is invalid", User.builder().name("ФИО").email("почта").age(25).build()),
                Arguments.of("Age is too low", User.builder().name("ФИО").email("test@mail.ru").age(MIN_USER_AGE - 1).build()),
                Arguments.of("Age is too high", User.builder().name("ФИО").email("test@mail.ru").age(MAX_USER_AGE + 1).build())
        );
    }

    @Test
    void registerUser_ShouldPassCorrectUserToDao_WhenHappyPath() {
        User userInService = User.builder().name("  ФИО  ").email("new@mail.ru").age(25).build();
        when(userDao.save(userInService)).thenReturn(55L);
        userService.registerUser(userInService);
        verify(userDao).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals("  ФИО  ", capturedUser.getName());
        assertEquals("new@mail.ru", capturedUser.getEmail());
        assertEquals(25, capturedUser.getAge());
    }

    @Test
    void updateUser_ShouldThrowUserAlreadyExistsException_WhenEmailIsDuplicate() {
        User user = User.builder().id(1L).name("ФИО").email("duplicate@mail.ru").age(25).build();
        RuntimeException sqlException = new RuntimeException("Unique constraint violation");
        RuntimeException hibernateException = new RuntimeException(
                "org.hibernate.exception.ConstraintViolationException: " + DB_UNIQUE_EMAIL_CONSTRAINT,
                sqlException
        );
        doThrow(hibernateException).when(userDao).update(user);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(user));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void getUserById_ShouldThrowValidationException_WhenIdIsInvalid(Long invalidId) {
        assertThrows(UserValidationException.class, () -> userService.getUserById(invalidId));
    }

    @Test
    void getUserById_ShouldThrowValidationException_WhenIdIsNull() {
        assertThrows(UserValidationException.class, () -> userService.getUserById(null));
    }

    @Test
    void getAllUsers_ShouldReturnList_WhenCalled() {
        List<User> expectedUsers = List.of(
                User.builder().id(1L).name("Грей").build(),
                User.builder().id(2L).name("Роуз").build()
        );
        when(userDao.findAll()).thenReturn(expectedUsers);
        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userDao).findAll();
    }

    @Test
    void updateUser_ShouldCallDaoUpdate_WhenUserIsValid() {
        User user = User.builder().id(1L).name("ФИО").email("new@mail.ru").age(25).build();
        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    void removeUserById_ShouldCallDaoDelete_WhenIdIsValid() {
        Long validId = 1L;
        userService.removeUserById(validId);
        verify(userDao).deleteById(validId);
    }

    @Test
    void registerUser_ShouldPropagateException_WhenGenericRuntimeExceptionOccurs() {
        User user = User.builder().name("ФИО").email("new@mail.ru").age(25).build();
        RuntimeException databaseCrash = new RuntimeException("Connection lost");
        when(userDao.save(user)).thenThrow(databaseCrash);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(user));

        assertEquals("Connection lost", exception.getMessage());
    }

    @Test
    void updateUser_ShouldThrowValidationException_WhenIdIsInvalid() {
        User userWithInvalidId = User.builder().id(0L).name("ФИО").email("new@mail.ru").age(25).build();

        assertThrows(UserValidationException.class, () -> userService.updateUser(userWithInvalidId));
    }

    @Test
    void updateUser_ShouldPropagateException_WhenGenericRuntimeExceptionOccurs() {
        User user = User.builder().id(1L).name("ФИО").email("new@mail.ru").age(25).build();
        RuntimeException genericError = new RuntimeException("Database timeout");
        doThrow(genericError).when(userDao).update(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(user));

        assertEquals("Database timeout", exception.getMessage());
    }

    @Test
    void removeUserById_ShouldThrowValidationException_WhenIdIsInvalid() {
        assertThrows(UserValidationException.class, () -> userService.removeUserById(-5L));
    }

    @Test
    void removeUserById_ShouldPropagateUserNotFoundException_WhenUserDoesNotExist() {
        Long nonExistingId = 999L;
        doThrow(new ru.aston.hometask2.exception.impl.UserNotFoundException("Not found"))
                .when(userDao).deleteById(nonExistingId);

        assertThrows(ru.aston.hometask2.exception.impl.UserNotFoundException.class, () ->
                userService.removeUserById(nonExistingId)
        );
    }


}
