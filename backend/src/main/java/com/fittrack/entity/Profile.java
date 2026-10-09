package com.fittrack.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a user's fitness profile and physical metrics.
 * Maps to the 'profiles' database table with a one-to-one relationship to {@link User}.
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    /**
     * Unique identifier for the profile.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Associated user account. Enforces one-to-one mapping per user.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    /**
     * Height of the user in centimeters.
     */
    private Double height;

    /**
     * Current weight of the user in kilograms.
     */
    private Double weight;

    /**
     * Target goal weight of the user in kilograms.
     */
    private Double goalWeight;

    /**
     * Target number of workouts per week.
     */
    private Integer weeklyTarget;
}
