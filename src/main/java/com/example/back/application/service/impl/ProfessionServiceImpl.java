package com.example.back.application.service.impl;

import com.example.back.application.dto.profession.ProfessionDto;
import com.example.back.application.service.ProfessionService;
import com.example.back.domain.model.Profession;
import com.example.back.domain.repository.ProfessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProfessionServiceImpl implements ProfessionService {

    private final ProfessionRepository professionRepository;

    public ProfessionServiceImpl(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    @Override
    public List<ProfessionDto> listAll() {
        return professionRepository.findAll().stream()
                .sorted(Comparator.comparing(Profession::getName, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new ProfessionDto(p.getId(), p.getName()))
                .collect(Collectors.toList());
    }
}
