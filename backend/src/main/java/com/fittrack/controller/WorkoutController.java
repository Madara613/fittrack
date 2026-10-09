package com.fittrack.controller;

import com.fittrack.dto.WorkoutRequest;
import com.fittrack.dto.WorkoutResponse;
import com.fittrack.exception.UnauthorizedException;
import com.fittrack.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing RESTful endpoints for logging, retrieving, modifying, and deleting user workouts.
 */
@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    /**
     * Creates a new workout session logged by the authenticated user.
     *
     * @param request        workout session parameters
     * @param authentication current security authentication token
     * @return 201 Created with persisted {@link WorkoutResponse}
     */
    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
            @RequestBody WorkoutRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        WorkoutResponse response = workoutService.createWorkout(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all workouts logged by the authenticated user in reverse chronological order.
     *
     * @param authentication current security authentication token
     * @return 200 OK with list of {@link WorkoutResponse}
     */
    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> listWorkouts(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        List<WorkoutResponse> workouts = workoutService.getUserWorkouts(email);
        return ResponseEntity.ok(workouts);
    }

    /**
     * Retrieves a single workout by its unique ID, ensuring it belongs to the authenticated user.
     *
     * @param id             unique identifier of the workout
     * @param authentication current security authentication token
     * @return 200 OK with {@link WorkoutResponse}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        WorkoutResponse response = workoutService.getWorkoutById(id, email);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing workout owned by the authenticated user.
     *
     * @param id             unique identifier of the workout to modify
     * @param request        fields to update
     * @param authentication current security authentication token
     * @return 200 OK with updated {@link WorkoutResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponse> updateWorkout(
            @PathVariable Long id,
            @RequestBody WorkoutRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        WorkoutResponse response = workoutService.updateWorkout(id, request, email);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a workout owned by the authenticated user.
     *
     * @param id             unique identifier of the workout to remove
     * @param authentication current security authentication token
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        workoutService.deleteWorkout(id, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * Extracts and validates the authenticated user's email address from the security context.
     *
     * @param authentication current security authentication token
     * @return the verified email address
     * @throws UnauthorizedException if authentication is missing or principal name is null
     */
    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return authentication.getName();
    }
}
