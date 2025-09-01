package com.example.back.application.dto.profession;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProfessionDto {
    private UUID id;
    private String name;
}
