package com.farmatodo.checkout.orders.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank private String customerName;
    @Email @NotBlank private String customerEmail;
    @NotBlank private String shippingAddress;

    // Para el reto: usamos token; el campo cardNumber es opcional y no se persiste.
    private String cardNumber; // opcional (no se usa si hay token)
    @NotBlank private String paymentToken;
}
