package com.example.tms.repository;

import com.example.tms.repository.entities.Task;
import com.example.tms.repository.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

/**
 * Task repository
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByHeader(String header);
    Page<Task> findAllByStatus(String status, Pageable pageable);
    Page<Task> findAllByPriority(String priority, Pageable pageable);
    Page<Task> findAllByAuthor(User author, Pageable pageable);
    Page<Task> findAllByExecutors(List<User> executor, Pageable pageable);
}
