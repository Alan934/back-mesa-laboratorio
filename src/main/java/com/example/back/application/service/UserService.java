package com.example.back.application.service;

import com.example.back.application.dto.user.UserCreateRequest;
import com.example.back.application.dto.user.UserDto;
import com.example.back.application.dto.user.UserSelfUpdateRequest;
import com.example.back.application.dto.user.UserUpdateRequest;
import com.example.back.domain.model.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Crear usuario (solo ADMIN)
    UserDto create(UserCreateRequest request);

    // Obtener por id
    UserDto getById(UUID id);

    // Listar todos o por rol
    List<UserDto> listAll();
    List<UserDto> listByRole(Role role);
    List<UserDto> listPractitionersByProfessionId(UUID professionId);

    // Actualizar usuario (solo ADMIN)
    UserDto update(UUID id, UserUpdateRequest request);

    // Actualizar mi perfil (CLIENT/PRACTITIONER/ADMIN)
    UserDto updateCurrent(UserSelfUpdateRequest request);

    // Eliminar usuario (solo ADMIN)
    void delete(UUID id);
}
