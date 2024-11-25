package com.example.tms.controllers;

import com.example.tms.dtos.TaskDto;
import com.example.tms.services.TaskService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;



@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@AllArgsConstructor
@RequestMapping("/api/v1/panel/admin")
public class AdminController {
    private final TaskService taskService;


    @PostMapping("/tasks/create")
    public ResponseEntity<?> createTask(Principal principal,
                                        @Validated @RequestBody TaskDto taskDto)
    {
        return taskService.createTask(principal, taskDto);
    }


    @PutMapping("/task/{taskHeader}/change/priority")
    @NotBlank
    public ResponseEntity<?> changePriority(@PathVariable String taskHeader,
                                            @RequestParam String priority)
    {
        return taskService.changePriority(taskHeader, priority);
    }


    @PutMapping("/task/{taskHeader}/change/description")
    @NotBlank
    public ResponseEntity<?> changeDescription(@PathVariable String taskHeader,
                                               @RequestParam String  description)
    {
        return taskService.changeDescription(taskHeader, description);
    }


    @PutMapping("/task/{taskHeader}/add/executors")
    @NotBlank
    public ResponseEntity<?> addExecutors(@PathVariable String taskHeader,
                                          @RequestParam List<String> executorNames)
    {
        return taskService.addExecutors(taskHeader, executorNames);
    }

    @PutMapping("/task/{taskHeader}/remove/executors")
    @NotBlank
    public ResponseEntity<?> removeExecutors(@PathVariable String taskHeader,
                                             @RequestParam List<String> executorNames)
    {
        return taskService.deleteExecutors(taskHeader, executorNames);
    }
}
