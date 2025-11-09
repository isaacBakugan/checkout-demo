package com.farmatodo.checkout.security.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesGcm {
    public static class EncData {
        public final String ivB64;
        public final String ctB64;

        EncData(String iv, String ct) {
            this.ivB64 = iv;
            this.ctB64 = ct;
        }
    }

    private final SecretKey key;
    private final SecureRandom rnd = new SecureRandom();

    public AesGcm(byte[] rawKey) {
        this.key = new SecretKeySpec(rawKey, "AES");
    }

    public EncData encrypt(String plaintext) {
        try {
            byte[] iv = new byte[12];
            rnd.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] out = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return new EncData(Base64.getEncoder().encodeToString(iv), Base64.getEncoder().encodeToString(out));
        } catch (Exception e) {
            throw new IllegalStateException("Encrypt error", e);
        }
    }

    public String decrypt(String ivB64, String ctB64) {
        try {
            byte[] iv = Base64.getDecoder().decode(ivB64);
            byte[] ct = Base64.getDecoder().decode(ctB64);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] out = c.doFinal(ct);
            return new String(out, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Decrypt error", e);
        }
    }

    public static byte[] generateKey256() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            SecretKey k = kg.generateKey();
            return k.getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}