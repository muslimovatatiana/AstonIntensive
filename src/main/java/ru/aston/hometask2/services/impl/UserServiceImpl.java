package ru.aston.hometask2.services.impl;

import ru.aston.hometask2.dao.UserDao;
import ru.aston.hometask2.models.User;
import ru.aston.hometask2.services.UserService;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Long registerUser(User user) {
        validateUser(user);
        return userDao.save(user);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        return userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            throw new IllegalArgumentException("Некорректный ID пользователя для обновления");
        }
        validateUser(user);
        userDao.update(user);
    }

    @Override
    public void removeUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Некорректный ID пользователя для удаления!");
        }
        userDao.deleteById(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Объект пользователя не может быть null!");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым!");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный формат email адреса!");
        }
        if (user.getAge() == null || user.getAge() < 0 || user.getAge() > 150) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 150 лет!");
        }
    }
}
