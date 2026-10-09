package com.fittrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents an individual workout session logged by a user.
 * Maps to the 'workouts' database table.
 */
@Entity
@Table(name = "workouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workout {

    /**
     * Unique identifier for the workout.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who recorded this workout session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    /**
     * Type/category of the workout (e.g. Running, Weightlifting, Yoga).
     */
    private String type;

    /**
     * Number of sets performed (primarily for strength training).
     */
    private Integer sets;

    /**
     * Number of repetitions performed per set.
     */
    private Integer reps;

    /**
     * Total duration of the workout session in minutes.
     */
    private Integer durationMinutes;

    /**
     * Estimated calories burned during the workout session.
     */
    private Integer calories;

    /**
     * Date when the workout took place.
     */
    private LocalDate date;
}
