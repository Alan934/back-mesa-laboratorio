package com.example.back.application.controller;

import com.example.back.application.dto.user.UserCreateRequest;
import com.example.back.application.dto.user.UserDto;
import com.example.back.application.dto.user.UserUpdateRequest;
import com.example.back.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Crear usuario (ADMIN)
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateRequest request, UriComponentsBuilder uriBuilder) {
        UserDto created = userService.create(request);
        return ResponseEntity.created(uriBuilder.path("/api/admin/users/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }

    // Listar usuarios (ADMIN)
    @GetMapping
    public List<UserDto> list() {
        return userService.listAll();
    }

    // Obtener un usuario por id (ADMIN)
    @GetMapping("/{id}")
    public UserDto get(@PathVariable UUID id) {
        return userService.getById(id);
    }

    // Actualizar un usuario (ADMIN)
    @PutMapping("/{id}")
    public UserDto update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    // Eliminar un usuario (ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
