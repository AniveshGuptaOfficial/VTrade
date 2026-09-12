package com.vtrade.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** groceries | beauty | stationery | undergarments */
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double price;

    private Double originalPrice;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    private String badge;

    /** "seller" if added by a student, "platform" if seeded */
    private String sourceType = "platform";

    /** FK to the user who added this product (nullable for platform items) */
    private Long sellerId;

    private String sellerName;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
