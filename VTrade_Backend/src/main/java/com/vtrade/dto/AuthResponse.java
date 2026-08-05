package com.vtrade.dto;

import com.vtrade.model.User;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
}
