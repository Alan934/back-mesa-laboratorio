package com.example.back.application.dto.appointment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// Petición de creación de turno por parte del cliente
@Getter
@Setter
@NoArgsConstructor
public class AppointmentCreateRequest {
    @NotNull
    private UUID practitionerId;
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;
    @Size(max = 500)
    private String description;

    public UUID getPractitionerId() { return practitionerId; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public String getDescription() { return description; }

    // Backward-compat accessors (record-style)
    public UUID practitionerId() { return practitionerId; }
    public LocalDateTime startAt() { return startAt; }
    public LocalDateTime endAt() { return endAt; }
    public String description() { return description; }
}
