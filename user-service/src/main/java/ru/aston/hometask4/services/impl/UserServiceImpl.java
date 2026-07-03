package ru.aston.hometask4.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.hometask4.clients.NotificationFeignClient;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.exceptions.ResourceNotFoundException;
import ru.aston.hometask4.mapping.UserMapper;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.User;
import ru.aston.hometask4.models.UserAction;
import ru.aston.hometask4.repositories.OutboxEventRepository;
import ru.aston.hometask4.repositories.UserRepository;
import ru.aston.hometask4.services.UserService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_NOT_FOUND_KEY = "user.not_found";

    private final UserRepository userRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final UserMapper userMapper;
    private final NotificationFeignClient notificationFeignClient;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        User user = userMapper.toEntity(requestDto);
        User savedUser = userRepository.save(user);
        saveOutboxEvent(UserAction.CREATE, savedUser.getEmail());
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_KEY, id));
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_KEY, id));
        userMapper.updateUserFromDto(requestDto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_KEY, id));
        userRepository.delete(user);
        saveOutboxEvent(UserAction.DELETE, user.getEmail());
    }

    @Override
    public void sendDirectNotification(UserAction action, String email) {
        notificationFeignClient.sendDirectNotification(action, email);
    }

    private void saveOutboxEvent(UserAction action, String email) {
        OutboxEvent event = OutboxEvent.builder()
                .action(action)
                .email(email)
                .build();
        outboxEventRepository.save(event);
    }
}
