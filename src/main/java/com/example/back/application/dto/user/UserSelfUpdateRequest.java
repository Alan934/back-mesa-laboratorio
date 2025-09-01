package com.example.back.application.dto.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSelfUpdateRequest {
    @Size(max = 100) private String firstName;
    @Size(max = 100) private String lastName;
    @Size(max = 50) private String dni;
    @Size(max = 30) private String phone;
    // For practitioners only
    @Size(max = 100) private String profession;
    private UUID professionId;
}
