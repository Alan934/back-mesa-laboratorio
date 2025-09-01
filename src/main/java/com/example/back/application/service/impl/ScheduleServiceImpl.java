package com.example.back.application.service.impl;

import com.example.back.application.dto.schedule.WorkingDayDto;
import com.example.back.application.dto.schedule.WorkingDayUpsertRequest;
import com.example.back.application.dto.schedule.WorkingIntervalDto;
import com.example.back.application.dto.schedule.WorkingIntervalUpsertRequest;
import com.example.back.application.service.ScheduleService;
import com.example.back.domain.model.User;
import com.example.back.domain.model.WorkingDay;
import com.example.back.domain.model.WorkingInterval;
import com.example.back.domain.repository.UserRepository;
import com.example.back.domain.repository.WorkingDayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final WorkingDayRepository workingDayRepository;
    private final UserRepository userRepository;

    public ScheduleServiceImpl(WorkingDayRepository workingDayRepository, UserRepository userRepository) {
        this.workingDayRepository = workingDayRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<WorkingDayDto> getSchedule(UUID practitionerId) {
        List<WorkingDay> days = workingDayRepository.findByPractitioner_IdOrderByDayOfWeekAsc(practitionerId);
        return days.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public boolean isWithinWorkingHours(UUID practitionerId, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) return false;
        LocalDate date = start.toLocalDate();
        if (!end.toLocalDate().isEqual(date)) return false; // force same-day appointments
        var dayOpt = workingDayRepository.findByPractitioner_IdAndDayOfWeek(practitionerId, start.getDayOfWeek());
        if (dayOpt.isEmpty()) return false;
        LocalTime s = start.toLocalTime();
        LocalTime e = end.toLocalTime();
        for (WorkingInterval wi : dayOpt.get().getIntervals()) {
            if (!wi.getEndTime().isAfter(wi.getStartTime())) continue;
            boolean within = ( !s.isBefore(wi.getStartTime()) ) && ( !e.isAfter(wi.getEndTime()) );
            if (within) return true;
        }
        return false;
    }

    @Override
    public void upsertSchedule(UUID practitionerId, List<WorkingDayUpsertRequest> days) {
        if (practitionerId == null) throw new IllegalArgumentException("Practitioner id is required");
        User practitioner = userRepository.findById(practitionerId).orElseThrow(() -> new IllegalArgumentException("Practitioner not found"));

        // Load existing days
        List<WorkingDay> existing = workingDayRepository.findByPractitioner_IdOrderByDayOfWeekAsc(practitionerId);
        Map<java.time.DayOfWeek, WorkingDay> byDow = existing.stream().collect(Collectors.toMap(WorkingDay::getDayOfWeek, d -> d));

        Set<java.time.DayOfWeek> incomingDows = new HashSet<>();
        for (WorkingDayUpsertRequest d : days) {
            if (d == null || d.getDayOfWeek() == null) continue;
            incomingDows.add(d.getDayOfWeek());
            WorkingDay entity = byDow.get(d.getDayOfWeek());
            if (entity == null) {
                entity = new WorkingDay();
                entity.setPractitioner(practitioner);
                entity.setDayOfWeek(d.getDayOfWeek());
                byDow.put(d.getDayOfWeek(), entity);
                existing.add(entity);
            }
            // Replace intervals
            entity.getIntervals().clear();
            if (d.getIntervals() != null) {
                for (WorkingIntervalUpsertRequest wi : d.getIntervals()) {
                    if (wi == null || wi.getStartTime() == null || wi.getEndTime() == null) continue;
                    if (!wi.getEndTime().isAfter(wi.getStartTime())) continue; // skip invalid
                    WorkingInterval interval = new WorkingInterval();
                    interval.setWorkingDay(entity);
                    interval.setStartTime(wi.getStartTime());
                    interval.setEndTime(wi.getEndTime());
                    entity.getIntervals().add(interval);
                }
            }
        }
        // Remove days not present in payload
        List<WorkingDay> toRemove = existing.stream().filter(d -> !incomingDows.contains(d.getDayOfWeek())).collect(Collectors.toList());
        existing.removeAll(toRemove);
        if (!toRemove.isEmpty()) {
            // JPA orphan removal for intervals handles intervals deletion
            toRemove.forEach(d -> {
                d.getIntervals().clear();
            });
        }
        // Persist changes
        existing.forEach(workingDayRepository::save);
        if (!toRemove.isEmpty()) {
            // Need a delete method; using EntityManager via repository deleteAll
            workingDayRepository.deleteAll(toRemove);
        }
    }

    private WorkingDayDto toDto(WorkingDay d) {
        List<WorkingIntervalDto> intervals = d.getIntervals().stream()
                .map(i -> new WorkingIntervalDto(i.getId(), i.getStartTime(), i.getEndTime()))
                .collect(Collectors.toList());
        return new WorkingDayDto(d.getId(), d.getDayOfWeek(), intervals);
    }
}
