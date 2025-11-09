package com.farmatodo.checkout.orders;

import com.farmatodo.checkout.audit.AuditService;
import com.farmatodo.checkout.cart.CartItem;
import com.farmatodo.checkout.cart.CartItemRepository;
import com.farmatodo.checkout.tokenization.CardTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final CardTokenRepository tokenRepo;
    private final PaymentSimulator payment;
    private final AuditService audit;

    public OrderService(OrderRepository orderRepo,
                        CartItemRepository cartRepo,
                        CardTokenRepository tokenRepo,
                        PaymentSimulator payment,
                        AuditService audit) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.tokenRepo = tokenRepo;
        this.payment = payment;
        this.audit = audit;
    }

    @Transactional
    public Order createFromCart(String sessionId, String name, String email, String address, String token) {
        List<CartItem> cart = cartRepo.findBySessionId(sessionId);
        if (cart.isEmpty()) throw new IllegalStateException("Carrito vacío");

        // Validar token existente
        tokenRepo.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Token inválido o no encontrado"));

        // Monto demo: sumamos quantity * price. Si price es 0, al menos contar ítems
        
        double amount = cart.stream().mapToDouble(ci -> (ci.getPrice() > 0 ? ci.getPrice() : 1.0) * ci.getQuantity()).sum();
        if (amount <= 0) amount = cart.stream().mapToInt(CartItem::getQuantity).sum();

        Order order = Order.builder()
                .sessionId(sessionId)
                .customerName(name)
                .customerEmail(email)
                .shippingAddress(address)
                .paymentToken(token)
                .status(OrderStatus.NEW)
                .amount(amount)
                .build();

        // Items
        
        for (var ci : cart) {
            double price = ci.getPrice() > 0 ? ci.getPrice() : 1.0;

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .sku(ci.getSku())
                    .name(ci.getName())
                    .quantity(ci.getQuantity())
                    .price(price)
                    .build();
            order.getItems().add(oi);
        }

        order = orderRepo.save(order);

        // Simular pago con reintentos
        boolean ok = payment.pay(token, amount);
        order.setStatus(ok ? OrderStatus.PAID : OrderStatus.DECLINED);

        // Persistir nuevo estado
        orderRepo.save(order);

        // Auditar
        String txId = MDC.get("txId");
        audit.log("orders", ok ? "payment_success" : "payment_failed",
                "orderId=" + order.getId() + ", amount=" + amount + ", token=" + token);

        // Si pago OK, limpiar carrito
        if (ok) {
            cartRepo.deleteAll(cart);
        }

        return order;
    }
}
