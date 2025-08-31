package com.example.back.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Entidad de turnos (appointments) que los clientes pueden solicitar
@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client; // Cliente que solicita el turno

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "practitioner_id")
    private User practitioner; // Profesional que atiende el turno (opcional hasta asignación)

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt; // inicio del turno

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt; // fin del turno

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(name = "description", length = 500)
    private String description; // descripción opcional
}
