package com.example.tms.services;

import com.example.tms.repository.RoleRepository;
import com.example.tms.repository.entities.Role;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    Optional<Role> findByName(String role)
    {
        return roleRepository.findByName(role);
    }



}
