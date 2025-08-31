package com.example.back.application.service;

import com.example.back.application.dto.appointment.AppointmentCreateRequest;
import com.example.back.application.dto.appointment.AppointmentDto;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    // Crear turno para el usuario actual (CLIENT)
    AppointmentDto createForCurrentUser(AppointmentCreateRequest request);

    // Listar mis turnos (CLIENT)
    List<AppointmentDto> listMyAppointments();

    // ADMIN: listar todos los turnos
    List<AppointmentDto> listAll();

    // ADMIN: aprobar
    AppointmentDto approve(UUID appointmentId);

    // ADMIN: cancelar
    AppointmentDto cancel(UUID appointmentId);
}
