package com.example.back.application.controller;

import com.example.back.application.dto.appointment.AppointmentDto;
import com.example.back.application.service.AppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/appointments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Listar todos los turnos (ADMIN)
    @GetMapping
    public List<AppointmentDto> listAll() {
        return appointmentService.listAll();
    }

    // Aprobar turno (ADMIN)
    @PostMapping("/{id}/approve")
    public AppointmentDto approve(@PathVariable UUID id) {
        return appointmentService.approve(id);
    }

    // Cancelar turno (ADMIN)
    @PostMapping("/{id}/cancel")
    public AppointmentDto cancel(@PathVariable UUID id) {
        return appointmentService.cancel(id);
    }
}
