package com.example.tms.controllers;

import com.example.tms.services.TaskService;
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
    public String showMyTasksToDo(Principal principal)
    {
        return taskService.showMyTasksToDo(principal);
    }

    @GetMapping("/get/tasks/of/{nickname}")
    public String showTasksOfUser(@PathVariable @RequestParam String nickname)
    {
        return taskService.showAllTasksOfUser(nickname);
    }

    @PostMapping("/task/{taskHeader}/change/status")
    public ResponseEntity<?> changeTaskStatus(Principal principal, @PathVariable @RequestParam String taskHeader, @RequestParam String status)
    {

        return taskService.changeTaskStatus(principal, taskHeader, status);
    }

    @PostMapping("/task/{taskHeader}/add/comment")
    public ResponseEntity<?> addComment(Principal principal, @PathVariable String taskHeader, @RequestParam String comment)
    {
        return taskService.addComment(principal, taskHeader, comment);
    }

}
