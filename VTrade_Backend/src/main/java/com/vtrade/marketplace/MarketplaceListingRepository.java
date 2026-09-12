package com.vtrade.marketplace;

import com.vtrade.marketplace.MarketplaceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, Long> {
    List<MarketplaceListing> findByCategoryAndStatus(String category, String status);
    List<MarketplaceListing> findByStatus(String status);
    List<MarketplaceListing> findBySellerId(Long sellerId);
}
