package com.example.tms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Default OK application data class
 */
@Data
@AllArgsConstructor
public class OkResponse {
    private String message;
    private HttpStatus status;
}
