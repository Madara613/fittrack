package com.fittrack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittrack.dto.ProfileRequest;
import com.fittrack.dto.SignupRequest;
import com.fittrack.repository.ProfileRepository;
import com.fittrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private String userToken;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.findByEmail("profiletest@example.com").ifPresent(user -> {
            profileRepository.findByUser(user).ifPresent(profileRepository::delete);
            userRepository.delete(user);
        });

        SignupRequest signup = SignupRequest.builder()
                .name("Profile Athlete")
                .email("profiletest@example.com")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated())
                .andReturn();

        var auth = objectMapper.readTree(result.getResponse().getContentAsString());
        userToken = auth.get("token").asText();
        userId = auth.get("userId").asLong();
    }

    @Test
    void unauthenticatedProfileRequestsShouldFail() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAndUpdateProfileFlow() throws Exception {
        // Initial GET
        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value("profiletest@example.com"))
                .andExpect(jsonPath("$.name").value("Profile Athlete"));

        // PUT update
        ProfileRequest updateReq = ProfileRequest.builder()
                .height(182.5)
                .weight(78.0)
                .goalWeight(74.0)
                .weeklyTarget(5)
                .build();

        mockMvc.perform(put("/profile")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.height").value(182.5))
                .andExpect(jsonPath("$.weight").value(78.0))
                .andExpect(jsonPath("$.goalWeight").value(74.0))
                .andExpect(jsonPath("$.weeklyTarget").value(5));

        // Subsequent GET verifies persistence
        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.height").value(182.5))
                .andExpect(jsonPath("$.weight").value(78.0))
                .andExpect(jsonPath("$.goalWeight").value(74.0))
                .andExpect(jsonPath("$.weeklyTarget").value(5));
    }
}
