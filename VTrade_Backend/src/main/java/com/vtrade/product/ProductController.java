package com.vtrade.product;

import com.vtrade.product.ProductRequest;
import com.vtrade.product.Product;
import com.vtrade.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(@RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(productService.search(search));
        }
        return ResponseEntity.ok(productService.getByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Product> create(@AuthenticationPrincipal Long userId,
                                           @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.create(userId, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@AuthenticationPrincipal Long userId,
                                                        @PathVariable Long id) {
        productService.delete(userId, id);
        return ResponseEntity.ok(Map.of("message", "Product deleted."));
    }
}
