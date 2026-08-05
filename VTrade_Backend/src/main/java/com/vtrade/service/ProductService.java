package com.vtrade.service;

import com.vtrade.dto.ProductRequest;
import com.vtrade.model.Product;
import com.vtrade.model.User;
import com.vtrade.repository.ProductRepository;
import com.vtrade.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getByCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("all")) {
            return productRepository.findAll();
        }
        return productRepository.findByCategory(category);
    }

    public List<Product> search(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public Product create(Long userId, ProductRequest req) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Product product = new Product();
        product.setName(req.getName());
        product.setCategory(req.getCategory());
        product.setPrice(req.getPrice());
        product.setOriginalPrice(req.getOriginalPrice());
        product.setStock(req.getStock());
        product.setImageUrl(req.getImageUrl());
        product.setBadge(req.getBadge());
        product.setSourceType("seller");
        product.setSellerId(seller.getId());
        product.setSellerName(seller.getFirstName());

        return productRepository.save(product);
    }

    public void delete(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));
        if (product.getSellerId() == null || !product.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own products.");
        }
        productRepository.delete(product);
    }
}
