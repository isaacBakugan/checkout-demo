package com.farmatodo.checkout.tokenization;

import com.farmatodo.checkout.tokenization.dto.TokenizeRequest;
import com.farmatodo.checkout.tokenization.dto.TokenizeResponse;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class TokenizationController {

    private final TokenizationService service;

    public TokenizationController(TokenizationService service) {
        this.service = service;
    }

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@Valid @RequestBody TokenizeRequest req) {
        String txId = UUID.randomUUID().toString();
        MDC.put("txId", txId);
        try {
            return ResponseEntity.ok(service.tokenize(req));
        } finally {
            MDC.remove("txId");
        }
    }
}