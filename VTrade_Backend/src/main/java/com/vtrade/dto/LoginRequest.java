package com.vtrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    @JsonProperty("student_id")
    private String studentId;
}