package com.esprit.userAuth.repositories;

import com.esprit.userAuth.entities.AppRole;
import com.esprit.userAuth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
    
    List<User> findByRole_RoleName(AppRole roleName);
}
