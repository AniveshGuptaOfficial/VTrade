package com.vtrade.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String phone;
    private String password;

    /** Login via Registration Number (student_id stored on User) */
    @JsonProperty("student_id")
    private String studentId;
}
