package com.fittrack.repository;

import com.fittrack.entity.Workout;
import com.fittrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Workout} entity database operations.
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /**
     * Finds all workouts belonging to a user ID.
     *
     * @param userId user identifier
     * @return list of workouts
     */
    List<Workout> findByUserId(Long userId);

    /**
     * Finds all workouts belonging to a user ID ordered by workout date descending.
     *
     * @param userId user identifier
     * @return sorted list of workouts
     */
    List<Workout> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Finds all workouts belonging to a user entity ordered by workout date descending.
     *
     * @param user user entity
     * @return sorted list of workouts
     */
    List<Workout> findByUserOrderByDateDesc(User user);
}
