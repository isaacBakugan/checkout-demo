package com.farmatodo.checkout.cart;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItem> getCart(@RequestHeader("X-SESSION-ID") String sessionId) {
        return cartService.getCart(sessionId);
    }

    @PostMapping("/{sku}")
    public CartItem addToCart(@PathVariable String sku,
                              @RequestHeader("X-SESSION-ID") String sessionId) {
        return cartService.addToCart(sessionId, sku);
    }
}
