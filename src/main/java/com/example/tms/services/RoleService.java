package com.example.tms.services;

import com.example.tms.repository.RoleRepository;
import com.example.tms.repository.entities.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;


/**
 * Role service for interaction with Roles of Users
 */
@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    /**
     * @return Role entity
     *
     */
    public Optional<Role> findByName(String role)
    {
        return roleRepository.findByName(role);
    }
}
