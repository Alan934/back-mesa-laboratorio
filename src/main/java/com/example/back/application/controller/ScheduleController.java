package com.example.back.application.controller;

import com.example.back.application.dto.schedule.WorkingDayDto;
import com.example.back.application.dto.schedule.WorkingDayUpsertRequest;
import com.example.back.application.service.CurrentUserService;
import com.example.back.application.service.ScheduleService;
import com.example.back.domain.model.Role;
import com.example.back.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final CurrentUserService currentUserService;

    public ScheduleController(ScheduleService scheduleService, CurrentUserService currentUserService) {
        this.scheduleService = scheduleService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PRACTITIONER','ADMIN')")
    public List<WorkingDayDto> getMySchedule() {
        User me = currentUserService.getOrCreateCurrentUser();
        return scheduleService.getSchedule(me.getId());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('PRACTITIONER','ADMIN')")
    public List<WorkingDayDto> upsertMySchedule(@Valid @RequestBody List<WorkingDayUpsertRequest> days) {
        User me = currentUserService.getOrCreateCurrentUser();
        if (me.getRole() != Role.PRACTITIONER && me.getRole() != Role.ADMIN) {
            // Prevent non-practitioners writing schedules
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Forbidden");
        }
        scheduleService.upsertSchedule(me.getId(), days);
        return scheduleService.getSchedule(me.getId());
    }
}
