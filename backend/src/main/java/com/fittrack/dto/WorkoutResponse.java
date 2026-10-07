package com.fittrack.dto;

import com.fittrack.entity.Workout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {

    private Long id;

    private Long userId;

    private String type;

    private Integer sets;

    private Integer reps;

    private Integer durationMinutes;

    private Integer calories;

    private LocalDate date;

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
