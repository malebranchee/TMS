package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeTaskDescriptionDTO {
    @NotEmpty
    private String description;
    public ChangeTaskDescriptionDTO(){};
}
