package com.yonder.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomErrorResponse {
    private final String message;
    private final int status;
    private final String error;
}
