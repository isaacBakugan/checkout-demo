package com.farmatodo.checkout.orders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PaymentSimulator {

    private final double rejectProbability;
    private final int maxRetries;
    private final Random rnd = new Random();

    public PaymentSimulator(
            @Value("${app.business.paymentRejectProbability:0.25}") double rejectProbability,
            @Value("${app.business.paymentMaxRetries:3}") int maxRetries) {
        this.rejectProbability = rejectProbability;
        this.maxRetries = maxRetries;
    }

    public boolean pay(String token, double amount) {
        for (int i = 0; i <= maxRetries; i++) {
            boolean ok = rnd.nextDouble() >= rejectProbability;
            if (ok) return true;
        }
        return false;
    }
}
