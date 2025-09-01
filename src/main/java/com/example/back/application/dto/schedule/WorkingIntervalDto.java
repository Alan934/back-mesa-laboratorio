package com.example.back.application.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WorkingIntervalDto {
    private UUID id;
    private LocalTime startTime;
    private LocalTime endTime;
}
