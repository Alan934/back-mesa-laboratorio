package com.example.back.application.dto.user;

import com.example.back.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO para crear usuarios (usado por ADMIN)
public record UserCreateRequest(
        @NotBlank @Email String email,
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 50) String dni,
        @Size(max = 30) String phone,
        @Size(max = 100) String profession,
        @NotNull Role role
) {}
