package ru.aston.hometask2.services;

import ru.aston.hometask2.models.User;
import java.util.List;

public interface UserService {
    Long registerUser(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(User user);
    void removeUserById(Long id);
}
