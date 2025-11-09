package com.farmatodo.checkout.bootstrap;

import com.farmatodo.checkout.products.Product;
import com.farmatodo.checkout.products.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"default","dev","test"}) // no se ejecuta en prod
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository repo;
    private final boolean seedEnabled;

    public DataSeeder(ProductRepository repo,
                      @Value("${app.seed.products:true}") boolean seedEnabled) {
        this.repo = repo;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            System.out.println("[DataSeeder] Seed desactivado por configuración.");
            return;
        }

        if (repo.count() > 0) {
            System.out.println("[DataSeeder] Productos ya existen, no se siembra nada.");
            return;
        }

        var items = List.of(
                Product.builder().sku("SKU-001").name("Café Premium 500g").stock(25).build(),
                Product.builder().sku("SKU-002").name("Té Verde 20 sobres").stock(40).build(),
                Product.builder().sku("SKU-003").name("Chocolate 70% 100g").stock(15).build(),
                Product.builder().sku("SKU-004").name("Galletas Avena 12u").stock(30).build(),
                Product.builder().sku("SKU-005").name("Agua 1.5L").stock(50).build(),
                Product.builder().sku("SKU-006").name("Galleta Oreo").stock(1).build()
        );

        repo.saveAll(items);
        System.out.println("[DataSeeder] 5 productos iniciales cargados.");
    }
}
