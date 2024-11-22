package com.example.tms.services;

import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    Optional<Task> findByHeader(String header)
    {
        return taskRepository.findByHeader(header);
    }
    Optional<Task> findByPriority(String priority)
    {
        return taskRepository.findByPriority(priority);
    }

    Optional<Task> findByAuthor(User author)
    {

        return taskRepository.findByAuthor(author);
    }

    Optional<Task> findByStatus(String status)
    {
        return taskRepository.findByStatus(status);
    }

    public String showMyTasksToDo(Principal principal)
    {
       try {
           User user = userService.findByLogin(principal.getName()).get();
           return user.getTasksToExecute().toString();
       } catch (NoSuchElementException e)
       {
           return "No tasks to do, chill :)";
       }
    }

    public String showAllTasksByAuthor(String authorName)
    {
        try {
            User user = userService.findByNickname(authorName).get();
            if (user.getTasksToManage().isEmpty()) {
                return String.format("%s has no task to manage!", authorName);
            }
            return user.getTasksToManage().toString();
        } catch (NoSuchElementException e)
        {
            return String.format("User %s not found :(", authorName);
        }
    }
}
