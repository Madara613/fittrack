package com.fittrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a historical body weight progress check-in logged by a user.
 * Maps to the 'progress_logs' database table.
 */
@Entity
@Table(name = "progress_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressLog {

    /**
     * Unique identifier for the progress log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who logged this progress measurement.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Recorded weight measurement in kilograms.
     */
    private Double weight;

    /**
     * Date of the progress check-in.
     */
    private LocalDate date;
}
