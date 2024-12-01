package com.example.tms.controllers;

import com.example.tms.dtos.ChangeTaskDescriptionDTO;
import com.example.tms.dtos.ChangeTaskPriorityDTO;
import com.example.tms.dtos.ExecutorNamesDTO;
import com.example.tms.dtos.TaskDto;
import com.example.tms.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;



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
    public ResponseEntity<?> createTask(
            Principal principal,
            @Parameter(name = "Creating of new task!", description = "To create new task you must have role ADMIN.")
            @Validated @RequestBody TaskDto taskDto
    )
    {
        return taskService.createTask(principal, taskDto);
    }

    @Operation(summary = "Changes task priority")
    @PutMapping("/task/{taskHeader}/change/priority")
    public ResponseEntity<?> changePriority(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy")
            @PathVariable String taskHeader,
            @Parameter(name = "New priority", description = "Available: HIGH, MEDIUM, LOW.")
            @RequestBody
            ChangeTaskPriorityDTO dto
    )
    {
        return taskService.changePriority(taskHeader, dto);
    }

    @Operation(summary = "Changes task description")
    @PutMapping("/task/{taskHeader}/change/description")
    public ResponseEntity<?> changeDescription(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy")
            @PathVariable String taskHeader,
            @Parameter(name = "New description",
                    example = "Its easy to do!",
                    description = "Insert new description for task")
            @RequestBody ChangeTaskDescriptionDTO dto
    )
    {
        return taskService.changeDescription(taskHeader, dto);
    }

    @Operation(summary = "Adds executors to the task", description = "If ")
    @PutMapping("/task/{taskHeader}/add/executors")
    public ResponseEntity<?> addExecutors(
            @PathVariable String taskHeader,
            @Parameter(name = "Executors name list", description = "Insert array of executor nicknames")
            @RequestBody ExecutorNamesDTO dto
    )
    {
        return taskService.addExecutors(taskHeader, dto);
    }

    @Operation(summary = "Removes executors from the task", description = "If after removing, executor list is empty, then status sets up 'WAITING'")
    @DeleteMapping("/task/{taskHeader}/remove/executors")
    public ResponseEntity<?> removeExecutors(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy")
            @PathVariable String taskHeader,
            @Parameter(name = "Executors name list")
            @RequestBody ExecutorNamesDTO dto
    )
    {
        return taskService.deleteExecutors(taskHeader, dto);
    }

    @Operation(summary = "Removes task", description = "Removes task if its status is 'WAITING' or 'CLOSED', return OK, else return BAD REQUEST")
    @DeleteMapping("/task/{taskHeader}/remove/task")
    public ResponseEntity<?> removeTask(
            @Parameter(name = "Task name that is @PathVariable value", example = "Deploy")
            @PathVariable String taskHeader
    )
    {
        return taskService.deleteTask(taskHeader);
    }

}
