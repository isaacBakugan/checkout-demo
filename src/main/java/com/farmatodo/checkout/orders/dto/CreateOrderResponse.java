package com.farmatodo.checkout.orders.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderResponse {
    private Long orderId;
    private String status;
    private Double amount;
}
