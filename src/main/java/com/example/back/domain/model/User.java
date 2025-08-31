package com.example.back.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Entidad de usuario vinculada a Auth0 mediante el campo auth0UserId
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_auth0_user_id", columnNames = {"auth0_user_id"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
        })
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "auth0_user_id", length = 100)
    private String auth0UserId; // sub de Auth0

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "dni", length = 50)
    private String dni;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "profession", length = 100)
    private String profession;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.CLIENT; // Por defecto cliente
}
