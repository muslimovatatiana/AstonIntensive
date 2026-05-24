package ru.aston.hometask2.controllers;

import ru.aston.hometask2.models.User;
import ru.aston.hometask2.services.UserService;
import java.util.List;

import static ru.aston.hometask2.util.AppMessages.MSG_UPDATE_SUCCESS;
import static ru.aston.hometask2.util.AppMessages.MSG_USER_LIST_EMPTY;
import static ru.aston.hometask2.util.AppMessages.MSG_USER_LIST_HEADER;
import static ru.aston.hometask2.util.AppMessages.PREFIX_ERROR;
import static ru.aston.hometask2.util.AppMessages.PREFIX_INFO;
import static ru.aston.hometask2.util.AppMessages.PREFIX_SYSTEM_ERROR;
import static ru.aston.hometask2.util.AppMessages.PREFIX_VALIDATION_ERROR;
import static ru.aston.hometask2.util.AppMessages.getMsgDeleteError;
import static ru.aston.hometask2.util.AppMessages.getMsgDeleteSuccess;
import static ru.aston.hometask2.util.AppMessages.getMsgFindSuccess;
import static ru.aston.hometask2.util.AppMessages.getMsgLoadListError;
import static ru.aston.hometask2.util.AppMessages.getMsgRegisterSuccess;
import static ru.aston.hometask2.util.AppMessages.getMsgUpdateError;

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
            System.out.println(getMsgRegisterSuccess(generatedId));
        } catch (IllegalArgumentException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + e.getMessage());
        }
    }

    public void showUserById(Long id) {
        try {
            User user = userService.getUserById(id);
            System.out.println(getMsgFindSuccess(user));
        } catch (IllegalArgumentException e) {
            System.out.println(PREFIX_INFO + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        }
    }

    public void showAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println(MSG_USER_LIST_EMPTY);
            } else {
                System.out.println(MSG_USER_LIST_HEADER);
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println(getMsgLoadListError(e.getMessage()));
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
            System.out.println(MSG_UPDATE_SUCCESS);
        } catch (IllegalArgumentException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(getMsgUpdateError(e.getMessage()));
        }
    }

    public void deleteUser(Long id) {
        try {
            userService.removeUserById(id);
            System.out.println(getMsgDeleteSuccess(id));
        } catch (IllegalArgumentException e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(getMsgDeleteError(e.getMessage()));
        }
    }
}
