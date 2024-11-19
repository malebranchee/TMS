package com.example.tms.repository.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "tasks")
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

    @Column(name = "comments") // Добавить связку с User (скорее всего будет хэш лист)
    private String comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tasks_x_users",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "executor_id")
    )
    private List<User> executors;

    @ManyToOne()
    @JoinTable(name = "tasks_x_users", inverseJoinColumns = @JoinColumn(name = "author_id"))
    private User author;

    @Override
    public String toString()
    {
        return String.format("ID: %d, Header: %s, Description: %s, Status: %s, Comments: %s," +
                "Executors: %s, Author: %s", id, header, description, status, comments, executors.toString(), author.toString());
    }

}
