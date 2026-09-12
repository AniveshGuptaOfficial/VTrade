package com.vtrade.marketplace;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketplace_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /** books | electronics | clothing | sports | others */
    @Column(nullable = false)
    private String category;

    /** Like New | Good | Fair | For Parts */
    @Column(nullable = false)
    private String condition;

    @Column(length = 1000)
    private String reasonForSelling;

    @Column(nullable = false)
    private Double price;

    private Double originalPrice;

    private String imageUrl;

    @Column(nullable = false)
    private Long sellerId;

    private String sellerName;

    /** active | sold | removed */
    private String status = "active";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
