package com.example.back.application.controller;

import com.example.back.application.dto.appointment.AppointmentCreateRequest;
import com.example.back.application.dto.appointment.AppointmentDto;
import com.example.back.application.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Listar mis turnos (CLIENT/PRACTITIONER/ADMIN ve los suyos)
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT','PRACTITIONER','ADMIN')")
    public List<AppointmentDto> listMine() {
        return appointmentService.listMyAppointments();
    }

    // Crear un turno (CLIENT)
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    public AppointmentDto create(@Valid @RequestBody AppointmentCreateRequest request) {
        return appointmentService.createForCurrentUser(request);
    }
}
