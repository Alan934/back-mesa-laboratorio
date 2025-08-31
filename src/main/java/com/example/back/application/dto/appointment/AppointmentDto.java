package com.example.back.application.dto.appointment;

import com.example.back.domain.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO de salida de turno
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private UUID id;
    private UUID clientId;
    private UUID practitionerId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
    private String description;
}
