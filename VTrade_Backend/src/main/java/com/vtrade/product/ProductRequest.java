package com.vtrade.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotNull
    @Positive
    private Double price;

    private Double originalPrice;

    @NotNull
    @Positive
    private Integer stock;

    private String imageUrl;

    private String badge;
}
