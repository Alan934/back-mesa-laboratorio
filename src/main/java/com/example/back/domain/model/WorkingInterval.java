package com.example.back.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "working_intervals")
@Getter
@Setter
@NoArgsConstructor
public class WorkingInterval extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "working_day_id", nullable = false)
    private WorkingDay workingDay;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
