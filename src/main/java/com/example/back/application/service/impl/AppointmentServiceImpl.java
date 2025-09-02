package com.example.back.application.service.impl;

import com.example.back.application.dto.appointment.AppointmentCreateRequest;
import com.example.back.application.dto.appointment.AppointmentDto;
import com.example.back.application.service.AppointmentService;
import com.example.back.application.service.CurrentUserService;
import com.example.back.application.service.ScheduleService;
import com.example.back.domain.exception.NotFoundException;
import com.example.back.domain.model.*;
import com.example.back.domain.repository.AppointmentRepository;
import com.example.back.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, CurrentUserService currentUserService, UserRepository userRepository, ScheduleService scheduleService) {
        this.appointmentRepository = appointmentRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    public AppointmentDto createForCurrentUser(AppointmentCreateRequest request) {
        // Soportar tanto getters como accessors estilo record
        LocalDateTime start = request.getStartAt() != null ? request.getStartAt() : request.startAt();
        LocalDateTime end = request.getEndAt() != null ? request.getEndAt() : request.endAt();
        validateTimes(start, end);
        User client = currentUserService.getOrCreateCurrentUser();

        if (appointmentRepository.existsOverlapping(client, start, end)) {
            throw new IllegalArgumentException("Overlapping appointment for the client");
        }

        UUID practitionerId = request.getPractitionerId() != null ? request.getPractitionerId() : request.practitionerId();
        User practitioner = userRepository.findById(practitionerId)
                .orElseThrow(() -> new NotFoundException("Practitioner not found"));
        if (practitioner.getRole() != Role.PRACTITIONER) {
            throw new IllegalArgumentException("Selected user is not a practitioner");
        }
        if (appointmentRepository.existsOverlappingForPractitioner(practitioner, start, end)) {
            throw new IllegalArgumentException("Overlapping appointment for the practitioner");
        }
        // Validate working hours
        if (!scheduleService.isWithinWorkingHours(practitioner.getId(), start, end)) {
            throw new IllegalArgumentException("Appointment is outside practitioner's working hours");
        }

        Appointment a = new Appointment();
        a.setClient(client);
        a.setPractitioner(practitioner);
        a.setStartAt(start);
        a.setEndAt(end);
        String description = request.getDescription() != null ? request.getDescription() : request.description();
        a.setDescription(description);
        a.setStatus(AppointmentStatus.PENDING);
        a = appointmentRepository.save(a);
        return toDto(a);
    }

    @Override
    public List<AppointmentDto> listMyAppointments() {
        User me = currentUserService.getOrCreateCurrentUser();
        if (me.getRole() == Role.PRACTITIONER) {
            return appointmentRepository.findByPractitionerId(me.getId()).stream().map(this::toDto).collect(Collectors.toList());
        }
        return appointmentRepository.findByClientId(me.getId()).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> listAll() {
        return appointmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AppointmentDto approve(UUID appointmentId) {
        Appointment a = getByIdOrThrow(appointmentId);
        a.setStatus(AppointmentStatus.APPROVED);
        return toDto(a);
    }

    @Override
    public AppointmentDto cancel(UUID appointmentId) {
        Appointment a = getByIdOrThrow(appointmentId);
        a.setStatus(AppointmentStatus.CANCELED);
        return toDto(a);
    }

    @Override
    public AppointmentDto approveMine(UUID appointmentId) {
        User me = currentUserService.getOrCreateCurrentUser();
        if (me.getRole() != Role.PRACTITIONER) {
            throw new IllegalArgumentException("Only practitioners can approve their own appointments");
        }
        Appointment a = getByIdOrThrow(appointmentId);
        if (a.getPractitioner() == null || !a.getPractitioner().getId().equals(me.getId())) {
            throw new IllegalArgumentException("Appointment does not belong to current practitioner");
        }
        a.setStatus(AppointmentStatus.APPROVED);
        return toDto(a);
    }

    @Override
    public AppointmentDto cancelMine(UUID appointmentId) {
        User me = currentUserService.getOrCreateCurrentUser();
        if (me.getRole() != Role.PRACTITIONER) {
            throw new IllegalArgumentException("Only practitioners can cancel their own appointments");
        }
        Appointment a = getByIdOrThrow(appointmentId);
        if (a.getPractitioner() == null || !a.getPractitioner().getId().equals(me.getId())) {
            throw new IllegalArgumentException("Appointment does not belong to current practitioner");
        }
        a.setStatus(AppointmentStatus.CANCELED);
        return toDto(a);
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("Invalid time range");
        }
    }

    private Appointment getByIdOrThrow(UUID id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    }

    private AppointmentDto toDto(Appointment a) {
        return new AppointmentDto(
                a.getId(),
                a.getClient().getId(),
                a.getPractitioner() != null ? a.getPractitioner().getId() : null,
                a.getStartAt(),
                a.getEndAt(),
                a.getStatus(),
                a.getDescription()
        );
    }
}
