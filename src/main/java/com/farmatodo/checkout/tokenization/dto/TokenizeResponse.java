package com.farmatodo.checkout.tokenization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenizeResponse {
    private String token;
    private String brand;
    private String last4;
    private Integer expMonth;
    private Integer expYear;
    private String createdAt;
}