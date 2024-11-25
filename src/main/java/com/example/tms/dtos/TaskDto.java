package com.example.tms.dtos;

import com.example.tms.repository.entities.User;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskDto {


    @NotBlank(message = "Header could not be blanked or empty value!")
    @Size(min = 5, max = 60, message = "Max length of header field is 60 characters, min is 5.")// 60
    private String header;

    @NotBlank(message = "Description could not be blanked or empty value!")
    @Size(min = 0, max = 100, message = "Max length of description field is 100 characters.")
    private String description;

    @NotBlank(message = "Status could not be blanked or empty value!")
    @Size(max = 30, message = "Max length of status field is 30 characters.")// 30
    private String status;

    @NotBlank(message = "Priority could not be blanked or empty value!")
    @Size(max = 30, message = "Max length of header field is 30 characters")//30
    private String priority;

    @NotBlank(message = "Executors name could not be blanked or empty value!")
    @Size(max = 30, message = "Max length of nickname is 30 characters.")
    private List<String> executorNicknames;

    @Nullable
    private User author;
}
