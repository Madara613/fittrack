package com.fittrack.service;

import com.fittrack.dto.ProfileRequest;
import com.fittrack.dto.ProfileResponse;
import com.fittrack.entity.Profile;
import com.fittrack.entity.User;
import com.fittrack.repository.ProfileRepository;
import com.fittrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

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

    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
