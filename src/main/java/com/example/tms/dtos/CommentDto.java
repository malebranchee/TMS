package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDto {
    @NotEmpty
    private String comment;
}
