package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeTaskStatusDTO {
    private String header;
    private String status;
}
