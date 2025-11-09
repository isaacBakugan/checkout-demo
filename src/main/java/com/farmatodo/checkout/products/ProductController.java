package com.farmatodo.checkout.products;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repo;
    private final int minStock;

    public ProductController(ProductRepository repo,
                             @Value("${app.business.minStockToShow:5}") int minStock) {
        this.repo = repo;
        this.minStock = minStock;
    }

    @GetMapping
    public List<Product> searchProducts(@RequestParam(required = false) String q) {
        var all = repo.findAll();
        return all.stream()
                .filter(p -> p.getStock() >= minStock)
                .filter(p -> q == null || p.getName().toLowerCase().contains(q.toLowerCase()))
                .toList();
    }
}
