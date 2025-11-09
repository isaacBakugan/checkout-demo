package com.farmatodo.checkout.tokenization;

import com.farmatodo.checkout.security.crypto.AesGcm;
import com.farmatodo.checkout.tokenization.dto.TokenizeRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.security.apiKeys=test-key")
public class TokenizationServiceTest {

    @Test
    void tokenize_ok() {
        var repo = Mockito.mock(CardTokenRepository.class);
        //Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> {
    CardToken ct = inv.getArgument(0);
    ct.setCreatedAt(Instant.now());
    return ct;
});
        var crypto = new AesGcm(AesGcm.generateKey256());
        //var svc = new TokenizationService(repo, crypto, 0.0);
        var audit = Mockito.mock(com.farmatodo.checkout.audit.AuditService.class);
        var svc = new TokenizationService(repo, crypto, audit, 0.0);
        var req = new TokenizeRequest();
        req.setCardNumber("4242424242424242");
        req.setCvv("123");
        req.setExpMonth(12);
        req.setExpYear(2099);
        req.setCardholderName("Test");

        var resp = svc.tokenize(req);
        assertThat(resp.getToken()).startsWith("tok_");
        assertThat(resp.getBrand()).isEqualTo("VISA");
        assertThat(resp.getLast4()).isEqualTo("4242");
    }
}