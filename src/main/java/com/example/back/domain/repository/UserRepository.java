package com.example.back.domain.repository;

import com.example.back.domain.model.Role;
import com.example.back.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuth0UserId(String auth0UserId);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
