package com.vtrade.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProvisionUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "A temporary password is required")
    @Size(min = 8, message = "Temporary password must be at least 8 characters")
    private String tempPassword;

    /** Must be "delivery_staff" or "store" — enforced in AdminService. */
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "delivery_staff|store", message = "Role must be 'delivery_staff' or 'store'")
    private String role;

    private String firstName;

    private String lastName;

    /** Optional — usually copied over from the person's access_requests submission. */
    @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
    private String phone;
}