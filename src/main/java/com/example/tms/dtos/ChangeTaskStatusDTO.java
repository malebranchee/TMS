package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ChangeTaskStatusDTO {
    @NotEmpty
    private String status;
    public ChangeTaskStatusDTO(){}
}
