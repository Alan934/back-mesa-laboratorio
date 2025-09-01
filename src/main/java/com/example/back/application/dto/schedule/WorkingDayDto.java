package com.example.back.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WorkingDayDto {
    private UUID id;
    private DayOfWeek dayOfWeek;
    private List<WorkingIntervalDto> intervals;
}
