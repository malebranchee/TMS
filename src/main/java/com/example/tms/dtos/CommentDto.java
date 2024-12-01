package com.example.tms.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {
    @NotEmpty
    private String comment;
    public CommentDto(){}
 }
