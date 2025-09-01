package com.example.back.application.controller;

import com.example.back.application.dto.profession.ProfessionDto;
import com.example.back.application.service.ProfessionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/professions")
public class ProfessionController {

    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public List<ProfessionDto> listAll() {
        return professionService.listAll();
    }
}
