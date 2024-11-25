package com.example.tms.controllers;

import com.example.tms.services.TaskService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/panel")
@AllArgsConstructor
public class UserController {
    private final TaskService taskService;

    @GetMapping("/get/tasks/my")
    @NotBlank
    public String showMyTasksToDo(Principal principal)
    {
            return taskService.showMyTasksToDo(principal);
    }

    @GetMapping("/get/tasks/of/{nickname}")
    @NotBlank
    public String showTasksOfUser(@PathVariable String nickname)
    {
        return taskService.showAllTasksOfUser(nickname);
    }

    @PutMapping("/task/{taskHeader}/change/status")
    @NotBlank
    public ResponseEntity<?> changeTaskStatus(Principal principal,
                                              @PathVariable String taskHeader,
                                              @RequestParam String status)
    {

        return taskService.changeTaskStatus(principal, taskHeader, status);
    }

    @PostMapping("/task/{taskHeader}/add/comment")
    @NotBlank
    public ResponseEntity<?> addComment(Principal principal,
                                        @PathVariable String taskHeader,
                                        @RequestParam String comment)
    {
        return taskService.addComment(principal, taskHeader, comment);
    }

}
