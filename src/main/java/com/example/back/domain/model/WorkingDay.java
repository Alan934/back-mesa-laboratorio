package com.example.back.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "working_days", uniqueConstraints = @UniqueConstraint(name = "uk_practitioner_day", columnNames = {"practitioner_id", "day_of_week"}))
@Getter
@Setter
@NoArgsConstructor
public class WorkingDay extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "practitioner_id", nullable = false)
    private User practitioner;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 16)
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "workingDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startTime ASC")
    private List<WorkingInterval> intervals = new ArrayList<>();
}
