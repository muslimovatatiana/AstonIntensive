package ru.aston.hometask2.exception.impl;

import ru.aston.hometask2.exception.UserServiceException;

public class UserValidationException extends UserServiceException {
    public UserValidationException(String message) {
        super(message);
    }
}
