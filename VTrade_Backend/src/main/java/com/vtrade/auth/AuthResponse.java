package com.vtrade.auth;

import com.vtrade.auth.User;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
}
