package com.fittrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {

    private String type;

    private Integer sets;

    private Integer reps;

    private Integer durationMinutes;

    private Integer calories;

    private LocalDate date;
}
