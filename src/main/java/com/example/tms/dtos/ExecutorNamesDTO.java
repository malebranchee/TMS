package com.example.tms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Executor names Data Class
 */
@Data
@AllArgsConstructor
public class ExecutorNamesDTO {
    List<String> executorNames;
    public ExecutorNamesDTO(){};
}
