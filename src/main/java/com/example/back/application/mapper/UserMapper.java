package com.example.back.application.mapper;

import com.example.back.application.dto.user.UserDto;
import com.example.back.domain.model.User;

// Mapper simple para convertir entidades User a DTOs
public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getAuth0UserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                user.getPhone(),
                user.getProfession(),
                user.getRole()
        );
    }
}
