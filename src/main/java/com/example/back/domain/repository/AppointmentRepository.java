package com.example.back.domain.repository;

import com.example.back.domain.model.Appointment;
import com.example.back.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByClientId(UUID clientId);
    List<Appointment> findByPractitionerId(UUID practitionerId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.client = :client AND ((:startAt < a.endAt) AND (:endAt > a.startAt))")
    boolean existsOverlapping(@Param("client") User client,
                              @Param("startAt") LocalDateTime startAt,
                              @Param("endAt") LocalDateTime endAt);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.practitioner = :practitioner AND ((:startAt < a.endAt) AND (:endAt > a.startAt))")
    boolean existsOverlappingForPractitioner(@Param("practitioner") User practitioner,
                                             @Param("startAt") LocalDateTime startAt,
                                             @Param("endAt") LocalDateTime endAt);
}
