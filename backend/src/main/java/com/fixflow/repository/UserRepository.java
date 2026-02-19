package com.fixflow.repository;

import com.fixflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    // Used to populate the "Assign Staff" dropdown in admin panel
    List<User> findByRole(String role);
}
