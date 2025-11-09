package com.farmatodo.checkout.cart;

import com.farmatodo.checkout.products.Product;
import com.farmatodo.checkout.products.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public List<CartItem> getCart(String sessionId) {
        return cartRepo.findBySessionId(sessionId);
    }

    public CartItem addToCart(String sessionId, String sku) {
        Optional<Product> productOpt = productRepo.findBySku(sku);
        if (productOpt.isEmpty())
            throw new IllegalArgumentException("Producto no encontrado: " + sku);

        Product p = productOpt.get();
        if (p.getStock() <= 0)
            throw new IllegalStateException("Sin stock");

        CartItem item = CartItem.builder()
                .sessionId(sessionId)
                .sku(p.getSku())
                .name(p.getName())
                .quantity(1)
                .price(0.0)
                .build();
        return cartRepo.save(item);
    }
}
