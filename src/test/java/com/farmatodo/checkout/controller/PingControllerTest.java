package com.farmatodo.checkout.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del endpoint /ping.
 * 
 * Valida que la ruta responda con estado HTTP 200 y el cuerpo esperado "pong".
 */

//@SpringBootTest
@SpringBootTest(properties = {
  "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.security.apiKeys=test-key")
class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Value("${app.security.apiKeys}")
    private String apiKey;

    @Test
    void shouldReturnPong() throws Exception {
        mockMvc.perform(get("/ping").header("X-API-KEY", "test-key"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
