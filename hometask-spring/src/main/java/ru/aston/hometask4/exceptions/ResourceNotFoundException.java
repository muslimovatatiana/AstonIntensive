package ru.aston.hometask4.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final Object[] args;

    public ResourceNotFoundException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
