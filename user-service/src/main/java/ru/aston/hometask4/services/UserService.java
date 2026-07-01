package ru.aston.hometask4.services;

import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.models.UserAction;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);
    UserResponseDto getUserById(UUID id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(UUID id, UserRequestDto requestDto);
    void deleteUserById(UUID id);
    void sendDirectNotification(UserAction action, String email);
}
