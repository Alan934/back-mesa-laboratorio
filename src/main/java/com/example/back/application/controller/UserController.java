package com.example.back.application.controller;

import com.example.back.application.dto.user.UserDto;
import com.example.back.application.mapper.UserMapper;
import com.example.back.application.service.CurrentUserService;
import com.example.back.application.service.UserService;
import com.example.back.domain.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public UserController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    // Devuelve el usuario actual y lo provisiona en la base local si no existe
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public UserDto me() {
        var me = currentUserService.getOrCreateCurrentUser();
        // Delegar el mapeo al servicio transaccional para evitar LazyInitializationException
        return userService.getById(me.getId());
    }

    @GetMapping("/practitioners")
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public List<UserDto> listPractitioners(@RequestParam(value = "professionId", required = false) UUID professionId) {
        if (professionId != null) {
            return userService.listPractitionersByProfessionId(professionId);
        }
        return userService.listByRole(Role.PRACTITIONER);
    }
}
