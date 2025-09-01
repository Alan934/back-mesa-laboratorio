package com.example.back.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Nueva entidad Profession: una profesión puede tener muchos practitioners
@Entity
@Table(name = "professions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_professions_name", columnNames = {"name"})
})
@Getter
@Setter
public class Profession extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
