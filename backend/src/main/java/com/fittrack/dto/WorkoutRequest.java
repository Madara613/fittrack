package com.fittrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data transfer object encapsulating workout creation and update attributes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {

    /**
     * Category/type of workout activity (e.g. "Running", "Weightlifting").
     */
    private String type;

    /**
     * Number of sets executed.
     */
    private Integer sets;

    /**
     * Repetitions executed per set.
     */
    private Integer reps;

    /**
     * Duration in minutes spent on the workout.
     */
    private Integer durationMinutes;

    /**
     * Estimated calories burned during the session.
     */
    private Integer calories;

    /**
     * Date of the workout session.
     */
    private LocalDate date;
}
