package com.example.tms.repository.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

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

    @NotBlank
    @UniqueElements(message = "")
    @Column(name = "header")
    private String header;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotBlank
    @Column(name = "status")
    private Status status;

    @NotBlank
    @Column(name = "priority")
    private Priority priority;

    @OneToMany(mappedBy = "task")
    @JoinTable(name = "tasks_x_users", inverseJoinColumns = @JoinColumn(name = "comment_id"))
    private List<Comment> comments;

    @ManyToMany(mappedBy = "tasksToExecute", fetch = FetchType.EAGER)
    @JoinTable(
            name = "tasks_x_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id")
    )
    private List<User> executors;

    @ManyToOne
    @JoinTable(name = "tasks_x_users", inverseJoinColumns = @JoinColumn(name = "author_id"))
    private User author;

    protected Task(){}

    public Task(String header, String description, Status status, Priority priority, List<User> executors, User author)
    {
        this.header = header;
        this.description = description;
        this.status = status;
        this.priority = priority;
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
        return String.format("ID: %d, Header: %s, Description: %s, Status: %s " +
                "Executors: %s, Author: %s, Comments: %s\n", id, header, description, status, executors.toString(),
                author.getNickname(), comments.toString()) ;
    }

}
