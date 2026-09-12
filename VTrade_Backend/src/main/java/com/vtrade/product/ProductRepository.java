package com.vtrade.product;

import com.vtrade.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findBySellerId(Long sellerId);
}
