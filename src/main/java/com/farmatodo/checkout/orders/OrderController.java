package com.farmatodo.checkout.orders;

import com.farmatodo.checkout.orders.dto.CreateOrderRequest;
import com.farmatodo.checkout.orders.dto.CreateOrderResponse;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@RequestHeader("X-SESSION-ID") String sessionId,
                                                      @Valid @RequestBody CreateOrderRequest req) {
        // txId para trazabilidad
        String txId = UUID.randomUUID().toString();
        MDC.put("txId", txId);
        try {
            Order o = service.createFromCart(sessionId, req.getCustomerName(), req.getCustomerEmail(),
                    req.getShippingAddress(), req.getPaymentToken());

            return ResponseEntity.ok(CreateOrderResponse.builder()
                    .orderId(o.getId())
                    .status(o.getStatus().name())
                    .amount(o.getAmount())
                    .build());
        } finally {
            MDC.remove("txId");
        }
    }
}
