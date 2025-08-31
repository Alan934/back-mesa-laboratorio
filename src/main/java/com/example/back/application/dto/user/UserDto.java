package com.example.back.application.dto.user;

import com.example.back.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

// DTO de salida para usuario
@Getter
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String auth0UserId;
    private String email;
    private String firstName;
    private String lastName;
    private String dni;
    private String phone;
    private String profession;
    private Role role;
}
