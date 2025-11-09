package com.farmatodo.checkout.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmatodo.checkout.auth.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import com.farmatodo.checkout.customers.Customer;
import com.farmatodo.checkout.customers.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
  "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.security.apiKeys=test-key")
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;
  @Autowired private CustomerRepository repo;
  @Autowired private PasswordEncoder encoder;

  @Value("${app.security.apiKeys}")
  private String apiKey;
/**
 * Pruebas unitarias del endpoint /auth/login.
 * 
 * Usuario de test
 */
  @BeforeEach
  void setupUser() {
    repo.deleteAll();
    Customer c = new Customer();
    c.setName("Isaac");
    c.setEmail("isaac@demo.com");
    c.setPhone("0414000000");
    c.setAddress("Caracas");
    c.setPasswordHash(encoder.encode("secret123"));
    repo.save(c);
  }
/**
 * Pruebas unitarias del endpoint /auth/login.
 * 
 * Login con usuario valido
 */
  @Test
  void loginShouldReturnToken_whenValidCredentials() throws Exception {
    var req = new LoginRequest();
    req.setEmail("isaac@demo.com");
    req.setPassword("secret123");

    mockMvc.perform(post("/auth/login")
        .header("X-API-KEY", "test-key")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated").value(true))
        .andExpect(jsonPath("$.token").exists());
  }
/**
 * Pruebas unitarias del endpoint /auth/login.
 * 
 * Login con contraseña incorrecta
 */
  @Test
  void loginShouldFail_whenBadPassword() throws Exception {
    var req = new LoginRequest();
    req.setEmail("isaac@demo.com");
    req.setPassword("wrong123456");

    mockMvc.perform(post("/auth/login")
        .header("X-API-KEY", "test-key")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(req)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
  }
}
