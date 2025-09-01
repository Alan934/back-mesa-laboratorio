package com.example.back.application.service.impl;

import com.example.back.application.dto.user.UserCreateRequest;
import com.example.back.application.dto.user.UserDto;
import com.example.back.application.dto.user.UserUpdateRequest;
import com.example.back.application.mapper.UserMapper;
import com.example.back.application.service.UserService;
import com.example.back.domain.exception.NotFoundException;
import com.example.back.domain.model.Profession;
import com.example.back.domain.model.Role;
import com.example.back.domain.model.User;
import com.example.back.domain.repository.ProfessionRepository;
import com.example.back.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfessionRepository professionRepository;

    public UserServiceImpl(UserRepository userRepository, ProfessionRepository professionRepository) {
        this.userRepository = userRepository;
        this.professionRepository = professionRepository;
    }

    @Override
    public UserDto create(UserCreateRequest request) {
        // Validación de unicidad por email
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        // Unicidad de DNI si viene informado
        if (request.dni() != null && !request.dni().isBlank()) {
            if (userRepository.findByDni(request.dni()).isPresent()) {
                throw new DataIntegrityViolationException("DNI already exists");
            }
        }
        Profession profession = null;
        if (request.role() == Role.PRACTITIONER) {
            profession = resolveProfessionOrThrow(request.professionId(), request.profession());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setDni(request.dni());
        user.setPhone(request.phone());
        user.setProfession(profession);
        user.setRole(request.role());
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    private Profession resolveProfessionOrThrow(UUID professionId, String professionName) {
        if (professionId != null) {
            return professionRepository.findById(professionId)
                    .orElseThrow(() -> new NotFoundException("Profession not found"));
        }
        if (professionName != null && !professionName.isBlank()) {
            return professionRepository.findByNameIgnoreCase(professionName)
                    .orElseGet(() -> {
                        Profession p = new Profession();
                        p.setName(professionName.trim());
                        return professionRepository.save(p);
                    });
        }
        throw new IllegalArgumentException("Profession is required for practitioners");
    }

    @Override
    public UserDto getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> listAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> listByRole(Role role) {
        return userRepository.findByRole(role).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> listPractitionersByProfessionId(UUID professionId) {
        return userRepository.findByRoleAndProfession_Id(Role.PRACTITIONER, professionId)
                .stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())) {
            userRepository.findByEmail(request.email()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DataIntegrityViolationException("Email already exists");
                }
            });
            user.setEmail(request.email());
        }
        if (request.dni() != null && (user.getDni() == null || !request.dni().equalsIgnoreCase(user.getDni()))) {
            if (userRepository.findByDni(request.dni()).isPresent()) {
                var existing = userRepository.findByDni(request.dni()).get();
                if (!existing.getId().equals(id)) {
                    throw new DataIntegrityViolationException("DNI already exists");
                }
            }
            user.setDni(request.dni());
        }
        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.phone() != null) user.setPhone(request.phone());

        // Profesión
        if (request.professionId() != null || (request.profession() != null && !request.profession().isBlank())) {
            user.setProfession(resolveProfessionOrThrow(request.professionId(), request.profession()));
        }

        // Rol
        if (request.role() != null) {
            if (request.role() == Role.PRACTITIONER) {
                // Verificar que el practitioner tenga profesión
                Profession current = user.getProfession();
                if (current == null) {
                    // Intentar resolver con datos de request si vinieron, si no, error
                    user.setProfession(resolveProfessionOrThrow(request.professionId(), request.profession()));
                }
            }
            user.setRole(request.role());
        }
        return UserMapper.toDto(user);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}
