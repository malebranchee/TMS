package com.example.tms.services;

import com.example.tms.repository.TaskRepository;
import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
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


}
