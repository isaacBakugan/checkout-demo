package com.farmatodo.checkout.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmatodo.checkout.cart.CartItem;
import com.farmatodo.checkout.cart.CartItemRepository;
import com.farmatodo.checkout.orders.dto.CreateOrderRequest;
import com.farmatodo.checkout.products.Product;
import com.farmatodo.checkout.products.ProductRepository;
import com.farmatodo.checkout.tokenization.CardToken;
import com.farmatodo.checkout.tokenization.CardTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.security.apiKeys=test-key",
        "app.business.paymentRejectProbability=0.0",  // que pague siempre
        "app.business.paymentMaxRetries=3",
        "app.business.minStockToShow=1",
        "app.security.cryptoKeyBase64=V4+2s8bK6bV3qZtbiJ+FQpKfYdMR3aJhKfGm7QW7dZ4=" // clave dummy
})
class OrderControllerHappyPathTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Autowired ProductRepository productRepo;
    @Autowired CartItemRepository cartRepo;
    @Autowired CardTokenRepository tokenRepo;

    private final String sessionId = "sess-test-001";
    private final String token = "tok_unit_123";

    @BeforeEach
    void setup() {
        cartRepo.deleteAll();
        productRepo.deleteAll();
        tokenRepo.deleteAll();

        // Producto con stock > 0
        productRepo.save(Product.builder()
                .sku("SKU-ORDER-1")
                .name("Producto Pedido")
                .stock(7)
                .build());

        // Ítem en carrito de la sesión
        cartRepo.save(CartItem.builder()
                .sessionId(sessionId)
                .sku("SKU-ORDER-1")
                .name("Producto Pedido")
                .quantity(2)
                .price(5.0) // demo
                .build());

        // Token existente (campos mínimos obligatorios)
        tokenRepo.save(CardToken.builder()
                .token(token)
                .brand("VISA")
                .last4("4242")
                .expMonth(12)
                .expYear(2099)
                .panCiphertextB64("x") // dummy
                .panIvB64("y")
                .cvvCiphertextB64("z")
                .cvvIvB64("w")
                .cardholderName("Test User")
                .customerEmail("test@example.com")
                .build());
    }

    @Test
    void create_order_from_cart_paid_and_decrease_stock() throws Exception {
        var req = new CreateOrderRequest();
        req.setCustomerName("Isaac");
        req.setCustomerEmail("isaac@example.com");
        req.setShippingAddress("Calle 123, Ciudad");
        req.setPaymentToken(token);
        req.setCardNumber(null); // no se usa, solo UI

        mvc.perform(post("/orders")
                        .header("X-API-KEY", "test-key")
                        .header("X-SESSION-ID", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        // Verificar que el stock bajó (7 - 2 = 5)
        var p = productRepo.findBySku("SKU-ORDER-1").orElseThrow();
        assertThat(p.getStock()).isEqualTo(5);

        // Opcional: el carrito debería limpiarse
        assertThat(cartRepo.findBySessionId(sessionId)).isEmpty();
    }
}
