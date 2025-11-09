package com.farmatodo.checkout.products;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "ux_products_sku", columnList = "sku", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40, unique = true)
    private String sku;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Integer stock;
}
