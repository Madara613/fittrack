package com.fittrack.service;

import com.fittrack.dto.ProfileRequest;
import com.fittrack.dto.ProfileResponse;
import com.fittrack.entity.Profile;
import com.fittrack.entity.User;
import com.fittrack.exception.ResourceNotFoundException;
import com.fittrack.exception.UnauthorizedException;
import com.fittrack.repository.ProfileRepository;
import com.fittrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling operations related to user fitness profiles and body metrics.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves the fitness profile belonging to the authenticated user.
     * If a profile does not yet exist, an empty profile is initialized and persisted.
     *
     * @param userEmail email of the authenticated user
     * @return {@link ProfileResponse} with current profile details
     * @throws UnauthorizedException if user email is not supplied
     * @throws ResourceNotFoundException if user record does not exist
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String userEmail) {
        User user = getUserByEmail(userEmail);

        Profile profile = profileRepository.findByUser(user)
                .orElseGet(() -> {
                    Profile newProfile = Profile.builder()
                            .user(user)
                            .build();
                    return profileRepository.save(newProfile);
                });

        return ProfileResponse.fromEntity(profile);
    }

    /**
     * Updates an existing profile's metrics (height, weight, goalWeight, weeklyTarget).
     *
     * @param request   DTO containing updated metrics
     * @param userEmail email of the authenticated user
     * @return {@link ProfileResponse} with updated metrics
     * @throws UnauthorizedException if user email is not supplied
     * @throws ResourceNotFoundException if user record does not exist
     */
    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request, String userEmail) {
        User user = getUserByEmail(userEmail);

        Profile profile = profileRepository.findByUser(user)
                .orElseGet(() -> Profile.builder()
                        .user(user)
                        .build());

        if (request.getHeight() != null) {
            profile.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            profile.setWeight(request.getWeight());
        }
        if (request.getGoalWeight() != null) {
            profile.setGoalWeight(request.getGoalWeight());
        }
        if (request.getWeeklyTarget() != null) {
            profile.setWeeklyTarget(request.getWeeklyTarget());
        }

        Profile saved = profileRepository.save(profile);
        return ProfileResponse.fromEntity(saved);
    }

    /**
     * Helper method to resolve and validate a {@link User} entity from their authenticated email.
     *
     * @param email user's email address
     * @return found {@link User} entity
     * @throws UnauthorizedException if email is null or empty
     * @throws ResourceNotFoundException if no user exists with the given email
     */
    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UnauthorizedException("User is not authenticated");
        }
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
