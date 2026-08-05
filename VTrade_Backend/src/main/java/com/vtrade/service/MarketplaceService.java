package com.vtrade.service;

import com.vtrade.dto.MarketplaceListingRequest;
import com.vtrade.model.MarketplaceListing;
import com.vtrade.model.User;
import com.vtrade.repository.MarketplaceListingRepository;
import com.vtrade.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketplaceService {

    private final MarketplaceListingRepository listingRepository;
    private final UserRepository userRepository;

    public MarketplaceService(MarketplaceListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    public List<MarketplaceListing> getAll(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("all")) {
            return listingRepository.findByStatus("active");
        }
        return listingRepository.findByCategoryAndStatus(category, "active");
    }

    public List<MarketplaceListing> getMine(Long userId) {
        return listingRepository.findBySellerId(userId);
    }

    public MarketplaceListing create(Long userId, MarketplaceListingRequest req) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        MarketplaceListing listing = new MarketplaceListing();
        listing.setTitle(req.getTitle());
        listing.setCategory(req.getCategory());
        listing.setCondition(req.getCondition());
        listing.setReasonForSelling(req.getReasonForSelling());
        listing.setPrice(req.getPrice());
        listing.setOriginalPrice(req.getOriginalPrice());
        listing.setImageUrl(req.getImageUrl());
        listing.setSellerId(seller.getId());
        listing.setSellerName((seller.getFirstName() == null ? "Student" : seller.getFirstName())
                + " " + (seller.getLastName() != null && !seller.getLastName().isBlank()
                        ? seller.getLastName().charAt(0) + "." : ""));
        listing.setStatus("active");

        return listingRepository.save(listing);
    }

    public MarketplaceListing markSold(Long userId, Long listingId) {
        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found."));
        if (!listing.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("You can only update your own listings.");
        }
        listing.setStatus("sold");
        return listingRepository.save(listing);
    }

    public void delete(Long userId, Long listingId) {
        MarketplaceListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found."));
        if (!listing.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("You can only delete your own listings.");
        }
        listingRepository.delete(listing);
    }
}
