package com.example.back.application.controller;

import com.example.back.application.dto.user.UserDto;
import com.example.back.application.service.UserService;
import com.example.back.domain.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/practitioners")
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public List<UserDto> listPractitioners() {
        return userService.listByRole(Role.PRACTITIONER);
    }
}
