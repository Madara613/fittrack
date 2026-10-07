package com.fittrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittrack.dto.SignupRequest;
import com.fittrack.dto.WorkoutRequest;
import com.fittrack.entity.User;
import com.fittrack.entity.Workout;
import com.fittrack.repository.ProfileRepository;
import com.fittrack.repository.UserRepository;
import com.fittrack.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private String user1Token;
    private Long user1Id;
    private String user2Token;
    private Long user2Id;

    @BeforeEach
    void setUp() throws Exception {
        workoutRepository.deleteAll();

        // Clean and setup user 1
        userRepository.findByEmail("user1@example.com").ifPresent(user -> {
            profileRepository.findByUser(user).ifPresent(profileRepository::delete);
            userRepository.delete(user);
        });

        // Clean and setup user 2
        userRepository.findByEmail("user2@example.com").ifPresent(user -> {
            profileRepository.findByUser(user).ifPresent(profileRepository::delete);
            userRepository.delete(user);
        });

        // Register user 1
        SignupRequest signupUser1 = SignupRequest.builder()
                .name("User One")
                .email("user1@example.com")
                .password("password123")
                .build();

        MvcResult result1 = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupUser1)))
                .andExpect(status().isCreated())
                .andReturn();

        var auth1 = objectMapper.readTree(result1.getResponse().getContentAsString());
        user1Token = auth1.get("token").asText();
        user1Id = auth1.get("userId").asLong();

        // Register user 2
        SignupRequest signupUser2 = SignupRequest.builder()
                .name("User Two")
                .email("user2@example.com")
                .password("password123")
                .build();

        MvcResult result2 = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupUser2)))
                .andExpect(status().isCreated())
                .andReturn();

        var auth2 = objectMapper.readTree(result2.getResponse().getContentAsString());
        user2Token = auth2.get("token").asText();
        user2Id = auth2.get("userId").asLong();
    }

    @Test
    void unauthenticatedRequestsShouldBeRejected() throws Exception {
        mockMvc.perform(get("/workouts"))
                .andExpect(status().isUnauthorized());

        WorkoutRequest request = WorkoutRequest.builder()
                .type("Cardio")
                .durationMinutes(30)
                .build();

        mockMvc.perform(post("/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAndListWorkoutsScopedToUser() throws Exception {
        WorkoutRequest request1 = WorkoutRequest.builder()
                .type("Running")
                .durationMinutes(30)
                .calories(300)
                .date(LocalDate.of(2026, 3, 1))
                .build();

        WorkoutRequest request2 = WorkoutRequest.builder()
                .type("Weightlifting")
                .sets(4)
                .reps(12)
                .durationMinutes(45)
                .calories(250)
                .date(LocalDate.of(2026, 3, 2))
                .build();

        // User 1 creates workout 1
        mockMvc.perform(post("/workouts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId").value(user1Id))
                .andExpect(jsonPath("$.type").value("Running"))
                .andExpect(jsonPath("$.durationMinutes").value(30))
                .andExpect(jsonPath("$.calories").value(300))
                .andExpect(jsonPath("$.date").value("2026-03-01"));

        // User 2 creates workout 2
        mockMvc.perform(post("/workouts")
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId").value(user2Id))
                .andExpect(jsonPath("$.type").value("Weightlifting"));

        // User 1 lists workouts -> should only see workout 1
        mockMvc.perform(get("/workouts")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("Running"))
                .andExpect(jsonPath("$[0].userId").value(user1Id));

        // User 2 lists workouts -> should only see workout 2
        mockMvc.perform(get("/workouts")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("Weightlifting"))
                .andExpect(jsonPath("$[0].userId").value(user2Id));
    }

    @Test
    void updateWorkoutByOwnerShouldSucceed() throws Exception {
        WorkoutRequest createRequest = WorkoutRequest.builder()
                .type("Cycling")
                .durationMinutes(20)
                .calories(150)
                .date(LocalDate.of(2026, 3, 5))
                .build();

        MvcResult createResult = mockMvc.perform(post("/workouts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long workoutId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        WorkoutRequest updateRequest = WorkoutRequest.builder()
                .type("Speed Cycling")
                .durationMinutes(40)
                .calories(320)
                .build();

        mockMvc.perform(put("/workouts/" + workoutId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workoutId))
                .andExpect(jsonPath("$.type").value("Speed Cycling"))
                .andExpect(jsonPath("$.durationMinutes").value(40))
                .andExpect(jsonPath("$.calories").value(320))
                .andExpect(jsonPath("$.date").value("2026-03-05"));
    }

    @Test
    void deleteWorkoutByOwnerShouldSucceed() throws Exception {
        WorkoutRequest createRequest = WorkoutRequest.builder()
                .type("Yoga")
                .durationMinutes(60)
                .build();

        MvcResult createResult = mockMvc.perform(post("/workouts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long workoutId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Delete by owner
        mockMvc.perform(delete("/workouts/" + workoutId)
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isNoContent());

        // Verify it no longer exists
        mockMvc.perform(get("/workouts")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void modifyingAnotherUsersWorkoutShouldReturnForbidden() throws Exception {
        WorkoutRequest createRequest = WorkoutRequest.builder()
                .type("Swimming")
                .durationMinutes(45)
                .build();

        // User 1 creates workout
        MvcResult createResult = mockMvc.perform(post("/workouts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long workoutId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // User 2 attempts to update User 1's workout
        WorkoutRequest updateRequest = WorkoutRequest.builder()
                .type("Hacked Swimming")
                .build();

        mockMvc.perform(put("/workouts/" + workoutId)
                        .header("Authorization", "Bearer " + user2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // User 2 attempts to delete User 1's workout
        mockMvc.perform(delete("/workouts/" + workoutId)
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonExistentWorkoutReturnsNotFound() throws Exception {
        WorkoutRequest updateRequest = WorkoutRequest.builder()
                .type("Running")
                .build();

        mockMvc.perform(put("/workouts/999999")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/workouts/999999")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isNotFound());
    }
}
