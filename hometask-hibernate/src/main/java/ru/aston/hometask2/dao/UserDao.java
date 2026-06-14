package ru.aston.hometask2.dao;

import ru.aston.hometask2.models.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Long save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void deleteById(Long id);
}
