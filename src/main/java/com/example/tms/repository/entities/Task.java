package com.example.tms.repository.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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

    @ManyToMany(mappedBy = "tasks")
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
        CREATED
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
        return String.format("ID: %d, Header: %s, Description: %s, Status: %s, " +
                "Executors: %s, Author: %s, Comments: %s\n", id, header, description, status, executors.stream().map(User::toString).toList(),
                author.getNickname(), comments.toString()) ;
    }

}
