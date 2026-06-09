package ru.aston.hometask2.exception.impl;

import ru.aston.hometask2.exception.UserServiceException;

public class UserAlreadyExistsException extends UserServiceException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
