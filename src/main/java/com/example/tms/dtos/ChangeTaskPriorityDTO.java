package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Task Priority Data Class
 */
@Data
@AllArgsConstructor
public class ChangeTaskPriorityDTO {

    @NotEmpty
    private String priority;
    public ChangeTaskPriorityDTO(){}
}
