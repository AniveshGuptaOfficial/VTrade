package com.vtrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleAuthRequest {
    @NotBlank(message = "Google ID token is required")
    @JsonProperty("idToken")
    private String idToken;

    /** "student" | "worker" | "store" — tells the backend which portal the user signed in from. */
    private String role;
}