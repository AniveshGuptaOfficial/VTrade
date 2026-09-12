package com.vtrade.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;

    @NotBlank
    @JsonProperty("hostel_block")
    private String hostelBlock;

    @NotBlank
    @JsonProperty("room_number")
    private String roomNumber;

    @NotBlank
    private String phone;

    private String landmark;

    @JsonProperty("payment_method")
    private String paymentMethod = "upi";

    @Data
    public static class OrderItemDto {
        private String name;

        @JsonProperty("unit_price")
        private Double unitPrice;

        private Integer quantity;
        private String image;
    }
}
