package com.farmatodo.checkout.security.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Bean
    public AesGcm aesGcm(@Value("${app.security.cryptoKeyBase64:}") String keyB64) {
        byte[] key;
        if (keyB64 != null && !keyB64.isBlank()) {
            key = Base64.getDecoder().decode(keyB64);
        } else {
            // Ephemeral key para dev si no se definió variable. No usar en prod.
            key = AesGcm.generateKey256();
            System.err.println("[WARN] APP_CRYPTO_KEY no definido. Se generó una clave efímera solo para DEV.");
        }
        if (key.length != 32)
            throw new IllegalStateException("AES key must be 32 bytes (256-bit)");
        return new AesGcm(key);
    }
}