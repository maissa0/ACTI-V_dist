package com.esprit.userAuth.repositories;

import com.esprit.userAuth.entities.AppRole;
import com.esprit.userAuth.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}