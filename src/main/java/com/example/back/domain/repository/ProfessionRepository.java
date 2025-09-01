package com.example.back.domain.repository;

import com.example.back.domain.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessionRepository extends JpaRepository<Profession, UUID> {
    Optional<Profession> findByNameIgnoreCase(String name);
}
