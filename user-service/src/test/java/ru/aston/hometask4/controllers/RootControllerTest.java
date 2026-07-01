package ru.aston.hometask4.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",

        "springdoc.api-docs.enabled=false",
        "springdoc.swagger-ui.enabled=false"
})
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnApiRootLinks() throws Exception {
        mockMvc.perform(get("/api/v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1"))
                .andExpect(jsonPath("$._links.users-url.href").value("http://localhost/api/v1/users"));
    }

}
