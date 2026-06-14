package ru.aston.hometask2.exception.impl;

import ru.aston.hometask2.exception.UserServiceException;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
