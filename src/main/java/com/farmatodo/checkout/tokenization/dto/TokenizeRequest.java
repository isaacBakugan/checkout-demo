package com.farmatodo.checkout.tokenization.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TokenizeRequest {
    @NotBlank
    @Size(min = 12, max = 19)
    private String cardNumber; // PAN

    @NotBlank
    @Size(min = 3, max = 4)
    private String cvv;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer expMonth;

    @NotNull
    @Min(2024)
    @Max(2100)
    private Integer expYear;

    @NotBlank
    private String cardholderName;

    @Email
    private String customerEmail; // opcional, para relacionar
}