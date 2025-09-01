package com.example.back.domain.repository;

import com.example.back.domain.model.WorkingDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkingDayRepository extends JpaRepository<WorkingDay, UUID> {

    @EntityGraph(attributePaths = "intervals")
    List<WorkingDay> findByPractitioner_IdOrderByDayOfWeekAsc(UUID practitionerId);

    @EntityGraph(attributePaths = "intervals")
    Optional<WorkingDay> findByPractitioner_IdAndDayOfWeek(UUID practitionerId, DayOfWeek dayOfWeek);
}
