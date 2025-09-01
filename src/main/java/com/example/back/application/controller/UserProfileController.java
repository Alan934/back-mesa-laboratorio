package com.example.back.application.controller;

import com.example.back.application.dto.user.UserDto;
import com.example.back.application.dto.user.UserSelfUpdateRequest;
import com.example.back.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public UserDto updateMe(@Valid @RequestBody UserSelfUpdateRequest request) {
        return userService.updateCurrent(request);
    }
}
