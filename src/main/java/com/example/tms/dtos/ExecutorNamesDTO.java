package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExecutorNamesDTO {
    List<String> executorNames;
    public ExecutorNamesDTO(){};
}
