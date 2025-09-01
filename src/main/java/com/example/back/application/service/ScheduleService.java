package com.example.back.application.service;

import com.example.back.application.dto.schedule.WorkingDayDto;
import com.example.back.application.dto.schedule.WorkingDayUpsertRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleService {
    List<WorkingDayDto> getSchedule(UUID practitionerId);
    boolean isWithinWorkingHours(UUID practitionerId, LocalDateTime start, LocalDateTime end);
    // Replace practitioner's schedule with provided days & intervals
    void upsertSchedule(UUID practitionerId, List<WorkingDayUpsertRequest> days);
}
