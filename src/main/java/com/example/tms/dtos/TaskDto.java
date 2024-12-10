package com.example.tms.dtos;

import com.example.tms.repository.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

/**
 * Default task presentation data class
 */
@Schema(name = "Task request entity")
@Data
@AllArgsConstructor
public class TaskDto {

    @Schema(name = "Header of task", minLength = 5, maxLength = 60, defaultValue = "Deploy")
    @NotEmpty(message = "Header could not be blanked or empty value!")
    @Size(min = 5, max = 60, message = "Max length of header field is 60 characters, min is 5.")// 60
    private String header;

    @Schema(name = "Description of task", minLength = 0, maxLength = 100, nullable = true)
    @NotEmpty(message = "Description could not be blanked or empty value!")
    @Size(min = 0, max = 100, message = "Max length of description field is 100 characters.")
    private String description;

    @Schema(name = "Status of task", allowableValues = {"CREATED", "IN_PROGRESS", "CLOSED"}, hidden = true)
    @NotEmpty(message = "Status could not be blanked or empty value!")
    @Size(max = 30, message = "Max length of status field is 30 characters")//30

    private String status;

    @Schema(name = "Priority of task", allowableValues = {"HIGH", "MEDIUM", "LOW"})
    @NotEmpty(message = "Priority could not be blanked or empty value!")
    @Size(max = 30, message = "Max length of header field is 30 characters")//30
    private String priority;

    @Schema(name = "List of executor names", maxLength = 30)
    private List<String> executorNicknames;

    @Schema(hidden = true)
    @Nullable
    private String author;

    @Schema(name = "List of comments", maxLength = 30, hidden = true)
    @Nullable
    private List<String> comments;

}
