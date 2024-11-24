package com.example.tms.controllers;

import com.example.tms.dtos.TaskDto;
import com.example.tms.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@AllArgsConstructor
@RequestMapping("/api/v1/panel/admin")
public class AdminController {
    private final TaskService taskService;

    // Create task
    @PostMapping("/tasks/create")
    public ResponseEntity<?> createTask(Principal principal, @RequestBody TaskDto taskDto)
    {
        return taskService.createTask(principal, taskDto);
    }

    @PutMapping("/task/{taskHeader}/change/priority")
    public ResponseEntity<?> changePriority(@PathVariable @RequestParam String taskHeader, @RequestParam String priority)
    {
        return taskService.changePriority(taskHeader, priority);
    }

    @PutMapping("/task/{taskHeader}/change/description")
    public ResponseEntity<?> changeDescription(@PathVariable @RequestParam String taskHeader, @RequestParam String description)
    {
        return taskService.changeDescription(taskHeader, description);
    }

    @PutMapping("/task/{taskHeader}/add/executors")
    public ResponseEntity<?> addExecutors(@PathVariable @RequestParam String taskHeader, @RequestParam List<String> executorNames)
    {
        return taskService.addExecutors(taskHeader, executorNames);
    }

    @PutMapping("/task/{taskHeader}/remove/executors")
    public ResponseEntity<?> removeExecutors(@PathVariable @RequestParam String taskHeader, @RequestParam List<String> executorNames)
    {
        return taskService.deleteExecutors(taskHeader, executorNames);
    }
}
