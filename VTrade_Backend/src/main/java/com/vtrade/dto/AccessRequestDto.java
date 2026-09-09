package com.vtrade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccessRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotBlank(message = "Requested role is required")
    @Pattern(regexp = "delivery_staff|store", message = "Role must be 'delivery_staff' or 'store'")
    private String requestedRole;

    private String message;
}