package com.example.tms.dtos;

import com.example.tms.repository.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskDto {
    private String header;
    private String description;
    private String status;
    private String priority;
    private List<String> executorNicknames;
    private User author;
}
