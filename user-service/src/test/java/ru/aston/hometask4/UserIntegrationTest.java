package ru.aston.hometask4;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.models.OutboxEvent;
import ru.aston.hometask4.models.OutboxStatus;
import ru.aston.hometask4.models.User;
import ru.aston.hometask4.models.UserAction;
import ru.aston.hometask4.repositories.OutboxEventRepository;
import ru.aston.hometask4.repositories.UserRepository;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private MessageSource messageSource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User existingUser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Ольга")
                .email("olga@mail.com")
                .age(25)
                .build();
        existingUser = userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        outboxEventRepository.deleteAllInBatch();
    }

    @Test
    void shouldCreateUserAndSaveOutboxEventWhenValid() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("  Дмитрий  ", "dima@mail.com", 28);

        String responseJson = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        UUID createdId = extractIdFromJson(responseJson);

        assertThat(userRepository.findById(createdId))
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user.getName()).isEqualTo("Дмитрий"));

        assertSingleOutboxEvent(UserAction.CREATE, "dima@mail.com");
    }

    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", existingUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingUser.getId().toString()))
                .andExpect(jsonPath("$.name").value("Ольга"))
                .andExpect(jsonPath("$.email").value("olga@mail.com"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Ольга"));
    }

    @Test
    void shouldUpdateUserWhenValid() throws Exception {
        UserRequestDto updateDto = new UserRequestDto("Ольга Обновленная", "olga_new@mail.com", 26);

        mockMvc.perform(put("/api/v1/users/{id}", existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ольга Обновленная"))
                .andExpect(jsonPath("$.age").value(26));

        assertThat(userRepository.findById(existingUser.getId()))
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user.getName()).isEqualTo("Ольга Обновленная"));
    }

    @Test
    void shouldDeleteUserAndSaveOutboxEvent() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", existingUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(existingUser.getId())).isFalse();
        assertSingleOutboxEvent(UserAction.DELETE, "olga@mail.com");
    }

    @Test
    void shouldReturnLocalized404WhenGetUserByNonExistentId() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String expectedMessage = getLocalizedMessage("user.not_found", nonExistentId, Locale.forLanguageTag("ru"));

        mockMvc.perform(get("/api/v1/users/{id}", nonExistentId)
                        .header("Accept-Language", "ru"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldReturnLocalized404WhenUpdateNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UserRequestDto updateDto = new UserRequestDto("Тест", "test@mail.com", 20);
        String expectedMessage = getLocalizedMessage("user.not_found", nonExistentId, Locale.ENGLISH);

        mockMvc.perform(put("/api/v1/users/{id}", nonExistentId)
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldReturnLocalized404WhenDeleteNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String expectedMessage = getLocalizedMessage("user.not_found", nonExistentId, Locale.forLanguageTag("ru"));

        mockMvc.perform(delete("/api/v1/users/{id}", nonExistentId)
                        .header("Accept-Language", "ru"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private UUID extractIdFromJson(String json) {
        String idStr = com.jayway.jsonpath.JsonPath.read(json, "$.id");
        return UUID.fromString(idStr);
    }

    private String getLocalizedMessage(String code, UUID id, Locale locale) {
        return messageSource.getMessage(code, new Object[]{id}, locale);
    }

    private void assertSingleOutboxEvent(UserAction expectedAction, String expectedEmail) {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);
        assertThat(outboxEvents).hasSize(1);
        assertThat(outboxEvents.get(0).getAction()).isEqualTo(expectedAction);
        assertThat(outboxEvents.get(0).getEmail()).isEqualTo(expectedEmail);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserDtos")
    void shouldCreateUserWithInvalidData(UserRequestDto invalidDto) throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserDtos")
    void shouldReturn400WhenUpdateUserWithInvalidData(UserRequestDto invalidDto) throws Exception {
        mockMvc.perform(put("/api/v1/users/{id}", existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findById(existingUser.getId()))
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user.getName()).isEqualTo("Ольга"));
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
