package com.vtrade.config;

import com.vtrade.model.Product;
import com.vtrade.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    /**
     * Set vtrade.seed.enabled=true in application.properties (or as an env var)
     * only when you deliberately want demo products inserted on a fresh database.
     * Defaults to false so restarts never silently repopulate data you deleted.
     */
    @Value("${vtrade.seed.enabled:false}")
    private boolean seedEnabled;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) return;
        if (productRepository.count() > 0) return;

        seed("Premium A5 Notebook", "stationery", 89.0, 120.0,
                "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400&h=400&fit=crop", "Trending");
        seed("Gel Pen Set (Pack of 12)", "stationery", 64.0, null,
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&h=400&fit=crop", null);
        seed("Highlighter Set (6 colours)", "stationery", 49.0, null,
                "https://images.unsplash.com/photo-1583947581924-860bda6a26df?w=400&h=400&fit=crop", null);
        seed("Himalaya Face Wash 100ml", "beauty", 95.0, 110.0,
                "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&h=400&fit=crop", "New");
        seed("Dove Shampoo 180ml", "beauty", 149.0, null,
                "https://images.unsplash.com/photo-1631390339432-74094fc2efec?w=400&h=400&fit=crop", null);
        seed("Nivea Men's Face Wash", "beauty", 109.0, null,
                "https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?w=400&h=400&fit=crop", null);
        seed("Maggi Noodles (Pack of 12)", "groceries", 144.0, null,
                "https://images.unsplash.com/photo-1612929633738-8fe44f7ec841?w=400&h=400&fit=crop", null);
        seed("Lays Classic (Pack of 6)", "groceries", 120.0, null,
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop", null);
        seed("Milk 500ml", "groceries", 28.0, null,
                "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&h=400&fit=crop", null);
        seed("Men's Cotton Briefs 3-Pack", "undergarments", 199.0, null,
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop", null);
        seed("Women's Innerwear Set", "undergarments", 249.0, null,
                "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=400&h=400&fit=crop", null);
        seed("A4 Ruled Notebook (Pack of 6)", "stationery", 120.0, null,
                "https://images.unsplash.com/photo-1517971053567-8bde93bc6a58?w=400&h=400&fit=crop", null);
    }

    private void seed(String name, String category, double price, Double original, String img, String badge) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setOriginalPrice(original);
        p.setStock(50);
        p.setImageUrl(img);
        p.setBadge(badge);
        p.setSourceType("platform");
        productRepository.save(p);
    }
}