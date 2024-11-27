package com.example.tms.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class OkResponse {
    private String message;
    private HttpStatus status;
}
