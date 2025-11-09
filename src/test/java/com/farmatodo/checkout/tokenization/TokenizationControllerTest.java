// package com.farmatodo.checkout.tokenization;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.farmatodo.checkout.tokenization.dto.TokenizeRequest;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.TestPropertySource;


// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// @TestPropertySource(properties = "app.security.apiKeys=test-key")
// public class TokenizationControllerTest {

//     @Autowired
//     MockMvc mvc;
//     @Autowired
//     ObjectMapper om;

    
//     @Test
//     void post_tokenize_200() throws Exception {
//         var req = new TokenizeRequest();
//         req.setCardNumber("4242424242424242");
//         req.setCvv("123");
//         req.setExpMonth(12);
//         req.setExpYear(2099);
//         req.setCardholderName("Test");

//         mvc.perform(post("/tokenize")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(om.writeValueAsString(req))
//                 .header("X-API-KEY", "test-key"))
//                 .andExpect(status().isOk());
//     }
// }
package com.farmatodo.checkout.tokenization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmatodo.checkout.tokenization.dto.TokenizeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.security.apiKeys=test-key",
        // Clave AES en base64 (solo para test)
        "app.security.cryptoKeyBase64=V4+2s8bK6bV3qZtbiJ+FQpKfYdMR3aJhKfGm7QW7dZ4=",
        // Sin rechazos aleatorios en test
        "app.business.tokenizationRejectProbability=0.0"
})
public class TokenizationControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @Test
    void post_tokenize_200() throws Exception {
        var req = new TokenizeRequest();
        req.setCardNumber("4242424242424242");
        req.setCvv("123");
        req.setExpMonth(12);
        req.setExpYear(2099);
        req.setCardholderName("Test");

        mvc.perform(post("/tokenize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .header("X-API-KEY", "test-key"))
                .andExpect(status().isOk());
    }
}
