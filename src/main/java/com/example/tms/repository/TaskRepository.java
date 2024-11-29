package com.example.tms.repository;

import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Optional<Task> findByHeader(String header);
    Optional<Task> findByPriority(Task.Priority priority);
    Optional<Task> findByAuthor(User author);
    Optional<Task> findByStatus(Task.Status status);
    Optional<Task> findByExecutors(List<User> executors);


}
