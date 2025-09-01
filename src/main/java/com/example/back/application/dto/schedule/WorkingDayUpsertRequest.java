package com.example.back.application.dto.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDayUpsertRequest {
    @NotNull private DayOfWeek dayOfWeek;
    @Valid @NotNull private List<WorkingIntervalUpsertRequest> intervals;
}
