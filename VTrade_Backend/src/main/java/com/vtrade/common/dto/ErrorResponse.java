package com.vtrade.common.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
}
