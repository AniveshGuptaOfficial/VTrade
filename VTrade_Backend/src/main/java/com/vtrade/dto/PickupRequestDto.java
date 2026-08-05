package com.vtrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PickupRequestDto {
    @NotBlank
    private String kartName;

    @NotBlank
    private String productDetails;

    private String trackingId;

    private String deliveryBoyName;

    private String deliveryBoyPhone;

    @NotNull
    @Positive
    private Double amount;

    @NotBlank
    private String paymentMode; // "paid" | "cod"

    @NotBlank
    private String requesterPhone;

    @NotBlank
    private String hostelBlock;

    @NotBlank
    private String roomNumber;

    private String notes;
}
