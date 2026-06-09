package ru.aston.hometask4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.hometask4.dto.UserRequestDto;
import ru.aston.hometask4.dto.UserResponseDto;
import ru.aston.hometask4.models.User;
import ru.aston.hometask4.repositories.UserRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Optional;
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
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        jdbcTemplate.execute("TRUNCATE TABLE spring.users RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldCreateUserWhenValid() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("  Дмитрий  ", "dima@mail.com", 28);

        String responseJson = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Дмитрий"))
                .andExpect(jsonPath("$.email").value("dima@mail.com"))
                .andExpect(jsonPath("$.age").value(28))
                .andReturn().getResponse().getContentAsString();

        UserResponseDto responseDto = objectMapper.readValue(responseJson, UserResponseDto.class);
        UUID createdId = responseDto.id();

        Optional<User> databaseUser = userRepository.findById(createdId);
        assertThat(databaseUser).isPresent();
        assertThat(databaseUser.get().getName()).isEqualTo("Дмитрий");
        assertThat(databaseUser.get().getEmail()).isEqualTo("dima@mail.com");
    }

    @Test
    void shouldGetUserById() throws Exception {
        String responseJson = mockMvc.perform(get("/api/v1/users/{id}", existingUser.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponseDto responseDto = objectMapper.readValue(responseJson, UserResponseDto.class);
        assertThat(responseDto.id()).isEqualTo(existingUser.getId());
        assertThat(responseDto.name()).isEqualTo("Ольга");
        assertThat(responseDto.email()).isEqualTo("olga@mail.com");
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        String responseJson = mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        UserResponseDto[] responseDtos = objectMapper.readValue(responseJson, UserResponseDto[].class);
        assertThat(responseDtos).hasSize(1);
        assertThat(responseDtos[0].name()).isEqualTo("Ольга");
    }

    @Test
    void shouldUpdateUserWhenValid() throws Exception {
        UserRequestDto updateDto = new UserRequestDto("Ольга Обновленная", "olga_new@mail.com", 26);

        String responseJson = mockMvc.perform(put("/api/v1/users/{id}", existingUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserResponseDto responseDto = objectMapper.readValue(responseJson, UserResponseDto.class);
        assertThat(responseDto.name()).isEqualTo("Ольга Обновленная");
        assertThat(responseDto.age()).isEqualTo(26);

        User updatedDbUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedDbUser.getName()).isEqualTo("Ольга Обновленная");
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", existingUser.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(existingUser.getId())).isFalse();
    }

    @Test
    void shouldReturnLocalized404WhenGetUserByNonExistentId() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        Locale localeRu = Locale.forLanguageTag("ru");
        String expectedMessage = messageSource.getMessage("user.not_found", new Object[]{nonExistentId}, localeRu);

        mockMvc.perform(get("/api/v1/users/{id}", nonExistentId)
                        .header("Accept-Language", "ru"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldReturnLocalized404WhenUpdateNonExistentUser() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        UserRequestDto updateDto = new UserRequestDto("Тест", "test@mail.com", 20);
        Locale localeEn = Locale.ENGLISH;
        String expectedMessage = messageSource.getMessage("user.not_found", new Object[]{nonExistentId}, localeEn);

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
        Locale localeRu = Locale.forLanguageTag("ru");
        String expectedMessage = messageSource.getMessage("user.not_found", new Object[]{nonExistentId}, localeRu);

        mockMvc.perform(delete("/api/v1/users/{id}", nonExistentId)
                        .header("Accept-Language", "ru"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserDtos")
    void shouldReturn400WhenCreateUserWithInvalidData(UserRequestDto invalidDto) throws Exception {
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

        User dbUserAfterFailedUpdate = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(dbUserAfterFailedUpdate.getName()).isEqualTo("Ольга");
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
                new UserRequestDto("Иван", "valid@mail.com", null));
    }
}
