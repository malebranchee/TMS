package com.example.tms.controllers;

import com.example.tms.dtos.ChangeTaskStatusDTO;
import com.example.tms.dtos.CommentDto;
import com.example.tms.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Tag(name = "UserTaskController", description = "Provides functions to user with role USER")
@ApiResponse(responseCode = "400", description = "Invalid or not existing value.")
@ApiResponse(responseCode = "401", description = "More likely wrong url, not enough authorities")
@ApiResponse(responseCode = "404", description = "More likely not existing mapping")
@ApiResponse(responseCode = "405", description = "Not allowed method")
@ApiResponse(responseCode = "200", description = "Its okay")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/panel")
@AllArgsConstructor
public class UserController {
    @Autowired
    private final TaskService taskService;

    @Operation(summary = "Getting all tasks of current user", description = "Return all tasks of current user in pagination.")
    @GetMapping("/tasks/get/my")
    public ResponseEntity<?> showMyTasksToDo(Principal principal,
                                             @Parameter(name = "Number of page")
                                             @RequestParam(defaultValue = "0") int page,
                                             @Parameter(name = "Size of elements on page")
                                             @RequestParam(defaultValue = "3") int size)
    {

        return taskService.showMyTasksToDo(principal, PageRequest.of(page, size));
    }


    @Operation(summary = "Getting all tasks of user", description = "Return all tasks of @PathVariable parameter user nickname")
    @GetMapping("/tasks/get/of/{nickname}")
    public ResponseEntity<?> showTasksOfUser(
                                            @Parameter(description = "Nickname parameter that is @PathVariable value.", example = "pablo")
                                            @PathVariable String nickname,
                                            @Parameter(name = "Number of page")
                                            @RequestParam(defaultValue = "0") int page,
                                            @Parameter(name = "Size of elements on page")
                                            @RequestParam(defaultValue = "3") int size)
    {
        return taskService.showAllTasksOfUser(nickname, PageRequest.of(page, size));
    }

    @Operation(summary = "Changing task status", description = "Put request to @PathVariable task header for changing task status.")
    @PutMapping("/tasks/{taskHeader}/change/status")
    public ResponseEntity<?> changeTaskStatus(Principal principal,
                                              @Parameter(description = "Task header parameter that is @PathVariable value.", example = "Deploy")
                                              @PathVariable String taskHeader,
                                              @Parameter(description = "New task status value. Available: HIGH, MEDIUM, LOW.", example = "HIGH")
                                              @RequestBody ChangeTaskStatusDTO dto)
    {

        return taskService.changeTaskStatus(principal, taskHeader, dto);
    }

    @Operation(summary = "Adding comment to task", description = "Post request to @PathVariable task header for adding new comment.")
    @PostMapping("/tasks/{taskHeader}/add/comment")
    public ResponseEntity<?> addComment(Principal principal,
                                        @Parameter(description = "Task header parameter that is @PathVariable value.", example = "Deploy")
                                        @PathVariable String taskHeader,
                                        @Parameter(description = "New comment which will be added to task comments.", example = "Too hard for me!")
                                        @RequestBody CommentDto dto)
    {
        return taskService.addComment(principal, taskHeader, dto);
    }

    @Operation(summary = "Paging and filtering tasks by status", description = "If 'status' not mentioned, returns all tasks")
    @GetMapping("/tasks/get/by/status")
    public ResponseEntity<?> getTasksByStatus(
            @Parameter(name = "Status to filter")
            @RequestParam(required = false) String status,
            @Parameter(name = "Number of page")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(name = "Size of elements on page")
            @RequestParam(defaultValue = "3") int size
    )
    {

        return taskService.searchTasksByStatus(status, PageRequest.of(page, size));
    }

    @Operation(summary = "Paging and filtering tasks by priority", description = "If 'priority' not mentioned, returns all tasks")
    @GetMapping("/tasks/get/by/priority")
    public ResponseEntity<?> getTasksByPriority(
            @Parameter(name = "Priority to filter")
            @RequestParam(required = false) String priority,
            @Parameter(name = "Number of page")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(name = "Size of elements on page")
            @RequestParam(defaultValue = "3") int size
    )
    {
        return taskService.searchTasksByPriority(priority, PageRequest.of(page, size));
    }
}
