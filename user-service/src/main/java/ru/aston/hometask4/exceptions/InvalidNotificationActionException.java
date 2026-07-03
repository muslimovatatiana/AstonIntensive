package ru.aston.hometask4.exceptions;

import lombok.Getter;

@Getter
public class InvalidNotificationActionException extends RuntimeException {

    private final Object[] args;

    public InvalidNotificationActionException(String message, Object... args) {
        super(message);
        this.args = args != null ? args : new Object[0];
    }
}
