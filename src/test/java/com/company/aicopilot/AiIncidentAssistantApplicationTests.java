package com.company.aicopilot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class AiIncidentAssistantApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void emptyQuestionShouldReturnBadRequest() throws Exception {

        String requestBody = """
                {
                    "question": ""
                }
                """;

        mockMvc.perform(
                        post("/api/chat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void questionExceeding2000CharactersShouldReturnBadRequest()
            throws Exception {

        String question = "a".repeat(2001);

        String requestBody = """
                {
                    "question": "%s"
                }
                """.formatted(question);

        mockMvc.perform(
                        post("/api/chat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chatWithoutAuthenticationShouldReturnUnauthorized()
            throws Exception {

        String requestBody = """
                {
                    "question": "test"
                }
                """;

        mockMvc.perform(
                        post("/api/chat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chatWithInvalidCredentialsShouldReturnUnauthorized()
            throws Exception {

        String requestBody = """
                {
                    "question": "test"
                }
                """;

        mockMvc.perform(
                        post("/api/chat")
                                .with(httpBasic("admin", "wrongpassword"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void chatWithValidCredentialsShouldPassAuthentication()
            throws Exception {

        String requestBody = """
                {
                    "question": ""
                }
                """;

        mockMvc.perform(
                        post("/api/chat")
                                .with(httpBasic("admin", "admin123"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.message")
                        .value("Question must not be empty"));
    }
}