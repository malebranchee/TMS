package com.example.tms.controllers;

import com.example.tms.services.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;


@AllArgsConstructor
@RestController
@PreAuthorize("USER")
@RequestMapping("/api/v1/panel")
public class UserController {
    private final TaskService taskService;

    @GetMapping("/get/tasks/my")
    public String showMyTasksToDo(Principal principal)
    {
        return taskService.showMyTasksToDo(principal);
    }

    @GetMapping("/get/tasks/by/author")
    public String showTasksOfAuthor(@RequestParam String authorName)
    {
        return taskService.showAllTasksByAuthor(authorName);
    }

}
