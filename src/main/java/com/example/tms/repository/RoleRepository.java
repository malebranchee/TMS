package com.example.tms.repository;

import com.example.tms.repository.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Role repository
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String role);

}
