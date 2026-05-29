package ru.aston.hometask2.controllers;

import ru.aston.hometask2.exception.impl.UserAlreadyExistsException;
import ru.aston.hometask2.exception.impl.UserNotFoundException;
import ru.aston.hometask2.exception.impl.UserValidationException;
import ru.aston.hometask2.models.User;
import ru.aston.hometask2.services.UserService;
import java.util.List;

import static ru.aston.hometask2.util.AppMessages.MSG_UPDATE_SUCCESS;
import static ru.aston.hometask2.util.AppMessages.MSG_USER_LIST_EMPTY;
import static ru.aston.hometask2.util.AppMessages.MSG_USER_LIST_HEADER;
import static ru.aston.hometask2.util.AppMessages.PREFIX_ERROR;
import static ru.aston.hometask2.util.AppMessages.PREFIX_SUCCESS;
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
            System.out.println(PREFIX_SUCCESS + getMsgRegisterSuccess(generatedId));
        } catch (UserValidationException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (UserAlreadyExistsException e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + e.getMessage());
        }
    }

    public void showUserById(Long id) {
        try {
            User user = userService.getUserById(id);
            System.out.println(PREFIX_SUCCESS + getMsgFindSuccess(user));
        } catch (UserValidationException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + e.getMessage());
        }
    }

    public void showAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println(PREFIX_SUCCESS + MSG_USER_LIST_EMPTY);
            } else {
                System.out.println(PREFIX_SUCCESS + MSG_USER_LIST_HEADER);
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + getMsgLoadListError(e.getMessage()));
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
            System.out.println(PREFIX_SUCCESS + MSG_UPDATE_SUCCESS);
        } catch (UserValidationException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (UserNotFoundException | UserAlreadyExistsException e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + getMsgUpdateError(e.getMessage()));
        }
    }

    public void deleteUser(Long id) {
        try {
            userService.removeUserById(id);
            System.out.println(PREFIX_SUCCESS + getMsgDeleteSuccess(id));
        } catch (UserValidationException e) {
            System.out.println(PREFIX_VALIDATION_ERROR + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println(PREFIX_ERROR + e.getMessage());
        } catch (Exception e) {
            System.out.println(PREFIX_SYSTEM_ERROR + getMsgDeleteError(e.getMessage()));
        }
    }
}
