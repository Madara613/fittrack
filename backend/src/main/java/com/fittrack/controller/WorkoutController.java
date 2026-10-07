package com.fittrack.controller;

import com.fittrack.dto.WorkoutRequest;
import com.fittrack.dto.WorkoutResponse;
import com.fittrack.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(
            @RequestBody WorkoutRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        WorkoutResponse response = workoutService.createWorkout(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> listWorkouts(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        List<WorkoutResponse> workouts = workoutService.getUserWorkouts(email);
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponse> getWorkout(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        WorkoutResponse response = workoutService.getWorkoutById(id, email);
        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        workoutService.deleteWorkout(id, email);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return authentication.getName();
    }
}
