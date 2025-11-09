package com.farmatodo.checkout.tokenization;

import com.farmatodo.checkout.security.crypto.AesGcm;
import com.farmatodo.checkout.tokenization.dto.TokenizeRequest;
import com.farmatodo.checkout.tokenization.dto.TokenizeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
public class TokenizationService {
    private final CardTokenRepository repo;
    private final AesGcm crypto;
    private final Random random = new Random();

    private final double rejectProbability;

    public TokenizationService(CardTokenRepository repo, AesGcm crypto,
            @Value("${app.business.tokenizationRejectProbability:0.0}") double rejectProbability) {
        this.repo = repo;
        this.crypto = crypto;
        this.rejectProbability = rejectProbability;
    }

    public TokenizeResponse tokenize(TokenizeRequest req) {
        validate(req);
        if (random.nextDouble() < rejectProbability) {
            throw new TokenizationRejectedException("Tokenization rejected by probability policy");
        }

        String brand = detectBrand(req.getCardNumber());
        String last4 = req.getCardNumber().substring(req.getCardNumber().length() - 4);
        var panEnc = crypto.encrypt(req.getCardNumber());
        var cvvEnc = crypto.encrypt(req.getCvv());

        String token = "tok_" + UUID.randomUUID();

        CardToken entity = CardToken.builder()
                .token(token)
                .brand(brand)
                .last4(last4)
                .expMonth(req.getExpMonth())
                .expYear(req.getExpYear())
                .panCiphertextB64(panEnc.ctB64)
                .panIvB64(panEnc.ivB64)
                .cvvCiphertextB64(cvvEnc.ctB64)
                .cvvIvB64(cvvEnc.ivB64)
                .cardholderName(req.getCardholderName())
                .customerEmail(req.getCustomerEmail())
                .build();

        entity = repo.save(entity);

        return TokenizeResponse.builder()
                .token(entity.getToken())
                .brand(entity.getBrand())
                .last4(entity.getLast4())
                .expMonth(entity.getExpMonth())
                .expYear(entity.getExpYear())
                .createdAt(entity.getCreatedAt().toString())
                .build();
    }

    private void validate(TokenizeRequest req) {
        if (!luhn(req.getCardNumber()))
            throw new IllegalArgumentException("Invalid card number");
        YearMonth exp = YearMonth.of(req.getExpYear(), req.getExpMonth());
        if (exp.isBefore(YearMonth.now()))
            throw new IllegalArgumentException("Card expired");
        if (!req.getCvv().matches("^\\d{3,4}$"))
            throw new IllegalArgumentException("Invalid CVV");
    }

    private boolean luhn(String pan) {
        int sum = 0;
        boolean alt = false;
        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = pan.charAt(i) - '0';
            if (alt) {
                n *= 2;
                if (n > 9)
                    n -= 9;
            }
            sum += n;
            alt = !alt;
        }
        return sum % 10 == 0;
    }

    private String detectBrand(String pan) {
        if (pan.startsWith("4"))
            return "VISA";
        if (pan.matches("^(5[1-5]).*"))
            return "MASTERCARD";
        if (pan.matches("^(2221|222[2-9]|22[3-9]\\d|2[3-6]\\d{2}|27[01]\\d|2720).*"))
            return "MASTERCARD";
        if (pan.matches("^3[47].*"))
            return "AMEX";
        return "UNKNOWN";
    }

    public class TokenizationRejectedException extends RuntimeException {
        public TokenizationRejectedException(String msg) {
            super(msg);
        }
    }
}