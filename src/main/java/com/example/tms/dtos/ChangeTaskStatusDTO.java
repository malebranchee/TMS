package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * Task Status Data Class
 */
@Data
@AllArgsConstructor
public class ChangeTaskStatusDTO {
    @NotEmpty
    private String status;
    public ChangeTaskStatusDTO(){}
}
