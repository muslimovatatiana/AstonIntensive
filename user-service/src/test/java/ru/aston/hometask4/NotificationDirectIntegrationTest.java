package ru.aston.hometask4;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

class NotificationDirectIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSendDirectNotificationViaOpenFeignSuccessfully() throws Exception {
        stubFor(WireMock.post(urlEqualTo("/api/v1/notifications"))
                .willReturn(aResponse()
                        .withStatus(202)));

        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "CREATE")
                        .param("email", "olga@mail.com"))
                .andExpect(status().isOk());

        verify(postRequestedFor(urlEqualTo("/api/v1/notifications"))
                .withRequestBody(matchingJsonPath("$.email", equalTo("olga@mail.com")))
                .withRequestBody(matchingJsonPath("$.subject", equalTo("Test Create Subject")))
                .withRequestBody(matchingJsonPath("$.message", equalTo("Test Create Text test-site"))));
    }

    @Test
    void shouldTriggerCircuitBreakerFallbackWhenNotificationServiceIsDown() throws Exception {
        stubFor(WireMock.post(urlEqualTo("/api/v1/notifications"))
                .willReturn(aResponse()
                        .withStatus(500)));

        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "DELETE")
                        .param("email", "olga@mail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400BadRequestWhenActionIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/users/notify")
                        .param("action", "UNKNOWN")
                        .param("email", "olga@mail.com"))
                .andExpect(status().isBadRequest());

        com.github.tomakehurst.wiremock.client.WireMock.verify(0,
                com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor(urlEqualTo("/api/v1/notifications")));
    }

}
