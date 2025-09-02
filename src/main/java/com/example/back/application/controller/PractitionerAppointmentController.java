package com.example.back.application.controller;

import com.example.back.application.dto.appointment.AppointmentDto;
import com.example.back.application.service.AppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/practitioner/appointments")
@PreAuthorize("hasRole('PRACTITIONER')")
public class PractitionerAppointmentController {

    private final AppointmentService appointmentService;

    public PractitionerAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{id}/approve")
    public AppointmentDto approveMine(@PathVariable UUID id) {
        return appointmentService.approveMine(id);
    }

    @PostMapping("/{id}/cancel")
    public AppointmentDto cancelMine(@PathVariable UUID id) {
        return appointmentService.cancelMine(id);
    }
}
