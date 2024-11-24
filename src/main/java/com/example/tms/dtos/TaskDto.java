package com.example.tms.dtos;

import com.example.tms.repository.entities.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String header;

    @NotBlank
    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String priority;

    @NotBlank
    private List<String> executorNicknames;

    @Nullable
    private User author;
}
