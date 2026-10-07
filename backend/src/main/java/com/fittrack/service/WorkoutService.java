package com.fittrack.service;

import com.fittrack.dto.WorkoutRequest;
import com.fittrack.dto.WorkoutResponse;
import com.fittrack.entity.User;
import com.fittrack.entity.Workout;
import com.fittrack.repository.UserRepository;
import com.fittrack.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkoutResponse createWorkout(WorkoutRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout type is required");
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

    @Transactional(readOnly = true)
    public List<WorkoutResponse> getUserWorkouts(String userEmail) {
        User user = getUserByEmail(userEmail);

        return workoutRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(WorkoutResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutResponse getWorkoutById(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this workout");
        }

        return WorkoutResponse.fromEntity(workout);
    }

    @Transactional
    public WorkoutResponse updateWorkout(Long id, WorkoutRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to modify this workout");
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

    @Transactional
    public void deleteWorkout(Long id, String userEmail) {
        User user = getUserByEmail(userEmail);

        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found with id: " + id));

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to modify this workout");
        }

        workoutRepository.delete(workout);
    }

    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
