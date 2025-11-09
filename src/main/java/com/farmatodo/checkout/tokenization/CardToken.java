package com.farmatodo.checkout.tokenization;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "card_tokens", indexes = {
        @Index(name = "ux_cardtoken_token", columnList = "token", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String token; // ej. tok_<uuid>

    @Column(length = 20)
    private String brand; // VISA/MASTERCARD/etc

    @Column(length = 4)
    private String last4;

    private Integer expMonth;
    private Integer expYear;

    // Datos encriptados
    @Lob
    @Column(name = "pan_ct", nullable = false)
    private String panCiphertextB64;

    @Column(name = "pan_iv", nullable = false)
    private String panIvB64;

    @Lob
    @Column(name = "cvv_ct", nullable = false)
    private String cvvCiphertextB64;

    @Column(name = "cvv_iv", nullable = false)
    private String cvvIvB64;

    // Metadatos opcionales
    private String cardholderName;
    private String customerEmail;

    @CreationTimestamp
    private Instant createdAt;
}