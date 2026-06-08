package ru.aston.hometask2.services.impl;

import ru.aston.hometask2.dao.UserDao;
import ru.aston.hometask2.exception.impl.UserAlreadyExistsException;
import ru.aston.hometask2.exception.impl.UserNotFoundException;
import ru.aston.hometask2.exception.impl.UserValidationException;
import ru.aston.hometask2.models.User;
import ru.aston.hometask2.services.UserService;
import java.util.List;
import java.util.regex.Pattern;

import static ru.aston.hometask2.util.AppConstants.DB_UNIQUE_EMAIL_CONSTRAINT;
import static ru.aston.hometask2.util.AppConstants.EMAIL_REGEX;
import static ru.aston.hometask2.util.AppConstants.MIN_USER_AGE;
import static ru.aston.hometask2.util.AppConstants.MAX_USER_AGE;
import static ru.aston.hometask2.util.AppMessages.ERROR_DELETE_ID_INVALID;
import static ru.aston.hometask2.util.AppMessages.ERROR_EMAIL_FORMAT;
import static ru.aston.hometask2.util.AppMessages.ERROR_ID_POSITIVE;
import static ru.aston.hometask2.util.AppMessages.ERROR_NAME_EMPTY;
import static ru.aston.hometask2.util.AppMessages.ERROR_UPDATE_ID_INVALID;
import static ru.aston.hometask2.util.AppMessages.ERROR_USER_NULL;
import static ru.aston.hometask2.util.AppMessages.getErrorAgeRange;
import static ru.aston.hometask2.util.AppMessages.getErrorEmailOccupied;
import static ru.aston.hometask2.util.AppMessages.getErrorEmailRegistered;
import static ru.aston.hometask2.util.AppMessages.getErrorUserNotFound;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final Pattern emailPattern;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        this.emailPattern = Pattern.compile(EMAIL_REGEX);
    }

    @Override
    public Long registerUser(User user) {
        validateUser(user);
        try {
            return userDao.save(user);
        } catch (RuntimeException e) {
            if (isDuplicateEmailException(e)) {
                throw new UserAlreadyExistsException(getErrorEmailRegistered(user.getEmail()));
            }
            throw e;
        }
    }

    @Override
    public User getUserById(Long id) {
        if (isIncorrectId(id)) {
            throw new UserValidationException(ERROR_ID_POSITIVE);
        }
        return userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException(getErrorUserNotFound(id)));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) {
        if (isIncorrectId(user.getId())) {
            throw new UserValidationException(ERROR_UPDATE_ID_INVALID);
        }
        validateUser(user);
        try {
            userDao.update(user);
        } catch (RuntimeException e) {
            if (isDuplicateEmailException(e)) {
                throw new UserAlreadyExistsException(getErrorEmailOccupied(user.getEmail()));
            }
            throw e;
        }
    }

    @Override
    public void removeUserById(Long id) {
        if (isIncorrectId(id)) {
            throw new UserValidationException(ERROR_DELETE_ID_INVALID);
        }
        userDao.deleteById(id);
    }

    private void validateUser(User user) {
        if (isIncorrectUser(user)) {
            throw new UserValidationException(ERROR_USER_NULL);
        }
        if (isIncorrectName(user.getName())) {
            throw new UserValidationException(ERROR_NAME_EMPTY);
        }

        user.setName(user.getName().trim());

        if (isIncorrectEmail(user.getEmail())) {
            throw new UserValidationException(ERROR_EMAIL_FORMAT);
        }
        if (isIncorrectAge(user.getAge())) {
            throw new UserValidationException(getErrorAgeRange(MIN_USER_AGE, MAX_USER_AGE));
        }
    }

    private boolean isIncorrectUser(User user) {
        return user == null;
    }

    private boolean isIncorrectId(Long id) {
        return id == null || id <= 0;
    }

    private boolean isIncorrectName(String name) {
        return name == null || name.trim().isEmpty();
    }

    private boolean isIncorrectEmail(String email) {
        return email == null || !emailPattern.matcher(email).matches();
    }

    private boolean isIncorrectAge(Integer age) {
        return age == null || age < MIN_USER_AGE || age > MAX_USER_AGE;
    }

    private boolean isDuplicateEmailException(Throwable t) {
        while (t != null) {
            String message = t.getMessage();
            if (message != null && message.contains(DB_UNIQUE_EMAIL_CONSTRAINT)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
