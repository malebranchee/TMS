package com.example.tms.repository.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "user_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "roles_x_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @ManyToMany(mappedBy = "executors", fetch = FetchType.EAGER)
    private List<Task> tasksToExecute;

    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private List<Task> tasksToManage;

    @OneToMany(mappedBy = "authorOfComment", fetch = FetchType.EAGER)
    private List<Comment> comments;

    public User() {
    }

    // Tasks to execute methods
    // -----------------------------------
    public void deleteAllTasksToExecute()
    {
        tasksToExecute.clear();
    }

    public void addTaskToExecute(Task taskToExecute)
    {
        if (isNull(taskToExecute))
            tasksToExecute = new ArrayList<>();
        tasksToExecute.add(taskToExecute);
    }

    public void deleteTaskToExecute(Task taskToExecute)
    {
        tasksToExecute.remove(taskToExecute);
    }
    // -----------------------------------
    // Tasks to manage methods
    // -----------------------------------

    public void deleteAllTasksToManage()
    {
        tasksToManage.clear();
    }

    public void addTaskToManage(Task taskToManage)
    {
        if (isNull(taskToManage))
            tasksToManage = new ArrayList<>();
        tasksToManage.add(taskToManage);
    }

    public void deleteTaskToManage(Task taskToManage)
    {
        tasksToManage.remove(taskToManage);
    }
    // -----------------------------------

    public void addRole(Role role) {
        if (isNull(roles))
            roles = new ArrayList<>();
        roles.add(role);
    }

    @Override
    public String toString()
    {
        return String.format("ID: %d, Login: %s, Roles : %s", id, login,roles.toString());
    }

}
