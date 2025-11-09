package com.farmatodo.checkout.cart;

import com.farmatodo.checkout.products.Product;
import com.farmatodo.checkout.products.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.security.apiKeys=test-key"
})
class CartControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ProductRepository productRepo;

    @BeforeEach
    void setup() {
        productRepo.deleteAll();
        productRepo.save(Product.builder()
                .sku("SKU-TEST")
                .name("Producto Test")
                .stock(10)
                .build());
    }

    @Test
    void add_to_cart_and_get_cart() throws Exception {
        String sessionId = "sess-123";

        // Agregar producto
        mvc.perform(post("/cart/SKU-TEST")
                        .header("X-API-KEY", "test-key")
                        .header("X-SESSION-ID", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-TEST"));

        // Obtener carrito
        mvc.perform(get("/cart")
                        .header("X-API-KEY", "test-key")
                        .header("X-SESSION-ID", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Producto Test"));
    }
}
