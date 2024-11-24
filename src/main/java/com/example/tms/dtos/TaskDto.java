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


    @NotBlank(message = "Header could not be blanked or empty value!")
    private String header;

    @NotBlank(message = "Description could not be blanked or empty value!")
    private String description;


    @NotBlank(message = "Status could not be blanked or empty value!")
    private String status;

    @NotBlank(message = "Priority could not be blanked or empty value!")
    private String priority;

    @NotBlank(message = "Executors name could not be blanked or empty value!")
    private List<String> executorNicknames;

    @Nullable
    private User author;
}
