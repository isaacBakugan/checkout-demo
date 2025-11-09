package com.farmatodo.checkout.customers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmatodo.checkout.customers.dto.CustomerRegisterRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.apiKeys=test-key")
class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  

  @Autowired
private CustomerRepository customerRepository;
  @BeforeEach
void cleanDb() {
    customerRepository.deleteAll();
}
/**
 * Pruebas unitarias del endpoint /customers.
 * 
 * Valida registro de cliente.
 */
  @Test
  void registerCustomer_shouldCreate() throws Exception {
    var req = new CustomerRegisterRequest();
    req.setName("Test User");
    req.setEmail("test@demo.com");
    req.setPhone("0414000000");
    req.setAddress("Caracas");
    req.setPassword("secret123");

    mockMvc.perform(post("/customers")
        .header("X-API-KEY", "test-key")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@demo.com"));
  }
}
