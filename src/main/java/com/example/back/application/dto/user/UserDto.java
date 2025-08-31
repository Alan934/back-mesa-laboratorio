package com.example.back.application.dto.user;

import com.example.back.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

// DTO de salida para usuario
public record UserDto(
        UUID id,
        String auth0UserId,
        @Email String email,
        String firstName,
        String lastName,
        Role role
) {}
