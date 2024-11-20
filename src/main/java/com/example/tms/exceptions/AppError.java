package com.example.tms.exceptions;

import lombok.Data;
import java.util.Date;

@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    public AppError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
    public AppError(String message)
    {
        this.message = message;
        this.timestamp = new Date();
    }
}
