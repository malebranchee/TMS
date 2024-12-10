package com.example.tms.repository.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Role entity
 */
@Entity
@Table(name = "roles")
@AllArgsConstructor
@Setter
@Getter
@SequenceGenerator(name = "role_seq", sequenceName = "role_id_seq", allocationSize = 1)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    protected Role(){}

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users;

    @Override
    public String toString()
    {
        return String.format("%s", name);
    }
}
