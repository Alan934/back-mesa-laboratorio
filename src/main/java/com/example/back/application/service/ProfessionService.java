package com.example.back.application.service;

import com.example.back.application.dto.profession.ProfessionDto;

import java.util.List;

public interface ProfessionService {
    List<ProfessionDto> listAll();
}