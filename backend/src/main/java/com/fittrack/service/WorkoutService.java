package com.fittrack.service;

import com.fittrack.dto.WorkoutRequest;
import com.fittrack.dto.WorkoutResponse;
import com.fittrack.entity.User;
import com.fittrack.entity.Workout;
import com.fittrack.exception.BadRequestException;
import com.fittrack.exception.ForbiddenException;
import com.fittrack.exception.ResourceNotFoundException;
import com.fittrack.exception.UnauthorizedException;
import com.fittrack.repository.UserRepository;
import com.fittrack.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service orchestrating CRUD operations and authorization rules for user workout sessions.
 */
@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    /**
     * Records and persists a new workout entry assigned to the authenticated user.
     *
     * @param request   DTO containing exercise type, sets, reps, duration, and calories
     * @param userEmail email of the authenticated user
     * @return {@link WorkoutResponse} containing persisted workout information
     * @throws BadRequestException if the workout type is null or blank
     * @throws UnauthorizedException if user email is not authenticated
     * @throws ResourceNotFoundException if user entity does not exist
     */
    @Transactional
    public WorkoutResponse createWorkout(WorkoutRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new BadRequestException("Workout type is required");
        }

        Workout workout = Workout.builder()
                .user(user)
                .type(request.getType().trim())
                .sets(request.getSets())
                .reps(request.getReps())
                .durationMinutes(request.getDurationMinutes())
                .calories(request.getCalories())
                .date(request.getDate() != null ? request.getDate() : LocalDate.now())
                .build();

        Workout saved = workoutRepository.save(workout);
        return WorkoutResponse.fromEntity(saved);
    }

    /**
     * Lists all workouts logged by the authenticated user in descending chronological order.
     *
     * @param userEmail email of the authenticated user
     * @return List of {@link WorkoutResponse} objects belonging solely to the requesting user
     * @throws UnauthorizedException if user email is not authenticated
     * @throws ResourceNotFoundException if user entity does not exist
     */
    @Transactional(readOnly = true)
    public List<WorkoutResponse> getUserWorkouts(String userEmail) {
        User user = getUserByEmail(userEmail);

        return workoutRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single workout by its unique identifier, verifying resource ownership.
     *
     * @param id        the unique workout ID
     * @param userEmail email of the authenticated user
     * @return {@link WorkoutResponse} of the requested workout
     * @throws ResourceNotFoundException if workout with the given ID cannot be found
     * @throws ForbiddenException        if the workout does not belong to the requesting user
     */
    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to access this workout");
        }

        return WorkoutResponse.fromEntity(workout);
    }

    /**
     * Updates fields of an existing workout entry, verifying that the current user owns it.
     *
     * @param id        the unique workout ID to update
     * @param request   DTO with optional fields to patch
     * @param userEmail email of the authenticated user
     * @return {@link WorkoutResponse} with updated workout attributes
     * @throws ResourceNotFoundException if workout with the given ID does not exist
     * @throws ForbiddenException        if the workout belongs to another user
     */
    @Transactional
    public WorkoutResponse updateWorkout(Long id, WorkoutRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to modify this workout");
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            workout.setType(request.getType().trim());
        }
        if (request.getSets() != null) {
            workout.setSets(request.getSets());
        }
        if (request.getReps() != null) {
            workout.setReps(request.getReps());
        }
        if (request.getDurationMinutes() != null) {
            workout.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getCalories() != null) {
            workout.setCalories(request.getCalories());
        }
        if (request.getDate() != null) {
            workout.setDate(request.getDate());
        }

        Workout updated = workoutRepository.save(workout);
        return WorkoutResponse.fromEntity(updated);
    }

    /**
     * Deletes a workout entry after verifying ownership.
     *
     * @param id        the unique workout ID to delete
     * @param userEmail email of the authenticated user
     * @throws ResourceNotFoundException if workout with the given ID does not exist
     * @throws ForbiddenException        if the workout belongs to another user
     */
    @Transactional
    public void deleteWorkout(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not authorized to modify this workout");
        }

        workoutRepository.delete(workout);
    }

    /**
     * Resolves and validates a {@link User} entity by email address.
     *
     * @param email user's email address
     * @return resolved {@link User} entity
     * @throws UnauthorizedException if email is null or empty
     * @throws ResourceNotFoundException if no user matches the given email
     */
    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
