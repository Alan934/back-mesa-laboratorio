package com.example.back.application.dto.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingIntervalUpsertRequest {
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
}
