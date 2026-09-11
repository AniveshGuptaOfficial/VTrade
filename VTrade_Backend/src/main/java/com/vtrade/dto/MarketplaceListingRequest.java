package com.vtrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MarketplaceListingRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String category;

    @NotBlank
    private String condition;

    @NotBlank
    private String reasonForSelling;

    @NotNull
    @Positive
    private Double price;

    private Double originalPrice;

    private String imageUrl;
}