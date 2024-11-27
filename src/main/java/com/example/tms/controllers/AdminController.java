package com.example.tms.controllers;

import com.example.tms.dtos.TaskDto;
import com.example.tms.services.TaskService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;


@Tag(name = "Admin task controller", description = "Provides full access to tasks for users with role ADMIN")
@ApiResponse(responseCode = "400", description = "Invalid or not existing value.")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@AllArgsConstructor
@RequestMapping("/api/v1/panel/admin")
public class AdminController {
    private final TaskService taskService;

    @Operation(summary = "Creates new task")
    @PostMapping("/tasks/create")
    public ResponseEntity<?> createTask(Principal principal,
                                        @Validated @RequestBody TaskDto taskDto)
    {
        return taskService.createTask(principal, taskDto);
    }

    @Operation(summary = "Changes task priority")
    @PutMapping("/task/{taskHeader}/change/priority")
    public ResponseEntity<?> changePriority(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy") @PathVariable String taskHeader,
                                            @Parameter(name = "New priority", description = "Available: IN_PROGRESS, WAITING, CLOSED.")
                                            @RequestParam @NotBlank String priority)
    {
        return taskService.changePriority(taskHeader, priority);
    }

    @Operation(summary = "Changes task description")
    @PutMapping("/task/{taskHeader}/change/description")
    public ResponseEntity<?> changeDescription(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy") @PathVariable String taskHeader,
                                               @Parameter(name = "New description", example = "Its easy to do!")
                                               @RequestParam @NotBlank String  description)
    {
        return taskService.changeDescription(taskHeader, description);
    }

    @Operation(summary = "Adds executors to the task")
    @PutMapping("/task/{taskHeader}/add/executors")
    public ResponseEntity<?> addExecutors(@PathVariable String taskHeader,
                                          @Parameter(name = "Executors name list")
                                          @RequestParam @NotBlank List<String> executorNames)
    {
        return taskService.addExecutors(taskHeader, executorNames);
    }

    @Operation(summary = "Removes executors from the task")
    @DeleteMapping("/task/{taskHeader}/remove/executors")
    public ResponseEntity<?> removeExecutors(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy")  @PathVariable String taskHeader,
                                             @Parameter(name = "Executors name list")
                                             @RequestParam @NotBlank List<String> executorNames)
    {
        return taskService.deleteExecutors(taskHeader, executorNames);
    }
}
