package com.example.tms.repository.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Task entity
 */
@Entity
@Getter
@Setter
@Table(name = "tasks")
@AllArgsConstructor
@SequenceGenerator(name = "task_seq", sequenceName = "task_id_seq", allocationSize = 1)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    private Long id;

    @Column(name = "header")
    private String header;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "priority")
    private String priority;

    @ManyToMany
    @JoinTable(
            name = "tasks_comments",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tasks_x_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id")
    )
    private List<User> executors;

    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    protected Task(){}

    public Task(String header, String description, Status status, Priority priority, List<User> executors, User author)
    {
        this.header = header;
        this.description = description;
        this.status = status.toString();
        this.priority = priority.toString();
        this.comments = new ArrayList<>();
        this.executors = executors;
        this.author = author;
    }

    public enum Status
    {
        CLOSED,
        IN_PROGRESS,
        CREATED,
        WAITING
    }
    public enum Priority
    {
        HIGH,
        MEDIUM,
        LOW
    }

    @Override
    public String toString()
    {
        return String.format("ID: %d, Header: %s, Description: %s, Status: %s, Priority: %s \n" +
                " Executors: %s, Author: %s, Comments: %s\n", id, header, description, status, priority, executors.stream().map(User::getNickname).toList(),
                author.getNickname(), comments.stream().map(Comment::toString).toList()) ;
    }

}
