package ru.aston.hometask2.controllers;

import ru.aston.hometask2.models.User;
import ru.aston.hometask2.services.UserService;
import java.util.List;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void createUser(String name, String email, int age) {
        try {
            User user = User.builder()
                    .name(name)
                    .email(email)
                    .age(age)
                    .build();
            Long generatedId = userService.registerUser(user);
            System.out.println("[УСПЕХ]: Пользователь успешно зарегистрирован с ID: " + generatedId);
        } catch (IllegalArgumentException e) {
            System.out.println("[ОШИБКА ВАЛИДАЦИИ]: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[СИСТЕМНАЯ ОШИБКА]: " + e.getMessage());
        }
    }

    public void showUserById(Long id) {
        try {
            User user = userService.getUserById(id);
            System.out.println("Найден пользователь: " + user);
        } catch (IllegalArgumentException e) {
            System.out.println("[ИНФО]: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ОШИБКА]: " + e.getMessage());
        }
    }

    public void showAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Список пользователей пока пуст.");
            } else {
                System.out.println("=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ ===");
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("[ОШИБКА]: Не удалось загрузить список: " + e.getMessage());
        }
    }

    public void updateUser(Long id, String name, String email, int age) {
        try {
            User user = User.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .age(age)
                    .build();
            userService.updateUser(user);
            System.out.println("[УСПЕХ]: Данные пользователя обновлены!");
        } catch (IllegalArgumentException e) {
            System.out.println("[ОШИБКА ВАЛИДАЦИИ]: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ОШИБКА]: Не удалось обновить: " + e.getMessage());
        }
    }

    public void deleteUser(Long id) {
        try {
            userService.removeUserById(id);
            System.out.println("[УСПЕХ]: Пользователь с ID " + id + " удален.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ОШИБКА]: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ОШИБКА]: Не удалось удалить: " + e.getMessage());
        }
    }
}
