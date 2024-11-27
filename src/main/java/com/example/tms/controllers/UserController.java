package com.example.tms.controllers;

import com.example.tms.dtos.ChangeTaskStatusDTO;
import com.example.tms.dtos.CommentDto;
import com.example.tms.services.TaskService;
import com.example.tms.services.UserService;
import com.example.tms.utils.JwtUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.security.Principal;
import java.util.stream.Collectors;



@Tag(name = "UserTaskController", description = "Provides functions to user with role USER")
@ApiResponse(responseCode = "400", description = "Invalid or not existing value.")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/panel")
@AllArgsConstructor
public class UserController {
    @Autowired
    private final TaskService taskService;

    @Operation(summary = "Getting all tasks of current user", description = "Return all tasks of current user.")
    @GetMapping("/get/tasks/my")
    public ResponseEntity<?> showMyTasksToDo(Principal principal)
    {
        return taskService.showMyTasksToDo(principal);
    }


    @Operation(summary = "Getting all tasks of user", description = "Return all tasks of @PathVariable parameter user nickname.")
    @GetMapping("/get/tasks/of/{nickname}")
    public ResponseEntity<?> showTasksOfUser(@Parameter(description = "Nickname parameter that is @PathVariable value.", example = "pablo")
                                                 @PathVariable String nickname)
    {
        return taskService.showAllTasksOfUser(nickname);
    }

    @Operation(summary = "Changing task status", description = "Put request to @PathVariable task header for changing task status.")
    @PutMapping("/task/{taskHeader}/change/status")
    public ResponseEntity<?> changeTaskStatus(Principal principal,
                                              @Parameter(description = "Task header parameter that is @PathVariable value.", example = "Deploy")
                                              @PathVariable String taskHeader,
                                              @Parameter(description = "New task status value. Available: HIGH, MEDIUM, LOW.", example = "HIGH")
                                              @RequestBody ChangeTaskStatusDTO dto)
    {

        return taskService.changeTaskStatus(principal, taskHeader, dto);
    }

    @Operation(summary = "Adding comment to task", description = "Post request to @PathVariable task header for adding new comment.")
    @PostMapping("/task/{taskHeader}/add/comment")
    public ResponseEntity<?> addComment(Principal principal,
                                        @Parameter(description = "Task header parameter that is @PathVariable value.", example = "Deploy")
                                        @PathVariable String taskHeader,
                                        @Parameter(description = "New comment which will be added to task comments.", example = "Too hard for me!")
                                        @RequestBody CommentDto dto)
    {
        return taskService.addComment(principal, taskHeader, dto);
    }

}
