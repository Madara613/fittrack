package com.fittrack.dto;

import com.fittrack.entity.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data transfer object returning workout details to client callers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {

    /**
     * Unique workout identifier.
     */
    private Long id;

    /**
     * ID of the user who logged the workout.
     */
    private Long userId;

    /**
     * Category/type of workout activity.
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
     * Duration in minutes.
     */
    private Integer durationMinutes;

    /**
     * Calories burned.
     */
    private Integer calories;

    /**
     * Date when the workout occurred.
     */
    private LocalDate date;

    /**
     * Maps a {@link Workout} entity into a clean {@link WorkoutResponse} DTO.
     *
     * @param workout entity instance
     * @return populated DTO or null if entity is null
     */
    public static WorkoutResponse fromEntity(Workout workout) {
        if (workout == null) {
            return null;
        }
        return WorkoutResponse.builder()
                .id(workout.getId())
                .userId(workout.getUser() != null ? workout.getUser().getId() : null)
                .type(workout.getType())
                .sets(workout.getSets())
                .reps(workout.getReps())
                .durationMinutes(workout.getDurationMinutes())
                .calories(workout.getCalories())
                .date(workout.getDate())
                .build();
    }
}
