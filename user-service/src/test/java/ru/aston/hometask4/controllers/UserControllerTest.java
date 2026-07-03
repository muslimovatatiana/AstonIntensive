package ru.aston.hometask4.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.exceptions.ResourceNotFoundException;
import ru.aston.hometask4.models.UserAction;
import ru.aston.hometask4.services.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        LiquibaseAutoConfiguration.class
})
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void shouldCreateUserWhenValid() throws Exception {
        UUID generatedId = UUID.randomUUID();
        UserRequestDto requestDto = new UserRequestDto("Иван", "ivan@mail.com", 30);
        UserResponseDto responseDto = new UserResponseDto(generatedId, "Иван", "ivan@mail.com", 30, LocalDateTime.now());

        Mockito.when(userService.createUser(Mockito.any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/users/" + generatedId))
                .andExpect(jsonPath("$._links.update.href").value("http://localhost/api/v1/users/" + generatedId))
                .andExpect(jsonPath("$._links.delete.href").value("http://localhost/api/v1/users/" + generatedId))
                .andExpect(jsonPath("$._links.all-users.href").value("http://localhost/api/v1/users"));

        Mockito.verify(userService).createUser(requestDto);
    }

    @Test
    void shouldGetUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseDto responseDto = new UserResponseDto(userId, "Мария", "maria@mail.com", 20, LocalDateTime.now());

        Mockito.when(userService.getUserById(userId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/users/" + userId));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponseDto responseDto = new UserResponseDto(userId, "Алексей", "alex@mail.com", 25, LocalDateTime.now());

        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResponseDtoList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.userResponseDtoList[0].id").value(userId.toString()))
                .andExpect(jsonPath("$._embedded.userResponseDtoList[0]._links.self.href").value("http://localhost/api/v1/users/" + userId))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/users"));
    }

    @Test
    void shouldUpdateUserWhenValid() throws Exception {
        UUID userId = UUID.randomUUID();
        UserRequestDto requestDto = new UserRequestDto("Иван Обновленный", "ivan_new@mail.com", 31);
        UserResponseDto responseDto = new UserResponseDto(userId, "Иван Обновленный", "ivan_new@mail.com", 31, LocalDateTime.now());

        Mockito.when(userService.updateUser(Mockito.eq(userId), Mockito.any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/users/" + userId));

        Mockito.verify(userService).updateUser(userId, requestDto);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();
        Mockito.doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        Mockito.when(userService.getUserById(userId))
                .thenThrow(new ResourceNotFoundException("Technical error message"));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserDtos")
    void shouldReturn400WhenCreateUserWithInvalidData(UserRequestDto invalidDto) throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserDtos")
    void shouldReturn400WhenUpdateUserWithInvalidData(UserRequestDto invalidDto) throws Exception {
        UUID userId = UUID.randomUUID();
        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldSendDirectNotificationSuccessfully() throws Exception {
        Mockito.doNothing().when(userService).sendDirectNotification(UserAction.CREATE, "ivan@mail.com");

        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "CREATE")
                        .param("email", "ivan@mail.com"))
                .andExpect(status().isOk());

        Mockito.verify(userService).sendDirectNotification(UserAction.CREATE, "ivan@mail.com");
    }

    @Test
    void shouldReturn400WhenSendNotificationWithInvalidAction() throws Exception {
        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "INVALID")
                        .param("email", "ivan@mail.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WhenServiceThrowsUnexpectedException() throws Exception {
        Mockito.doThrow(new RuntimeException("Database timeout or unexpected error"))
                .when(userService).sendDirectNotification(UserAction.CREATE, "ivan@mail.com");

        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "CREATE")
                        .param("email", "ivan@mail.com"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error. Please try again later."));
    }

    private static Stream<UserRequestDto> provideInvalidUserDtos() {
        return Stream.of(
                new UserRequestDto(null, "valid@mail.com", 30),
                new UserRequestDto("   ", "valid@mail.com", 30),
                new UserRequestDto("Иван", null, 30),
                new UserRequestDto("Иван", "   ", 30),
                new UserRequestDto("Иван", "not-an-email", 30),
                new UserRequestDto("Иван", "valid@mail.com", -1),
                new UserRequestDto("Иван", "valid@mail.com", 151),
                new UserRequestDto("Иван", "valid@mail.com", null)
        );
    }
}
