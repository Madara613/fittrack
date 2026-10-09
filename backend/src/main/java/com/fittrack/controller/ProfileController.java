package com.fittrack.controller;

import com.fittrack.dto.ProfileRequest;
import com.fittrack.dto.ProfileResponse;
import com.fittrack.exception.UnauthorizedException;
import com.fittrack.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing endpoints for retrieving and updating user fitness profiles.
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Retrieves the fitness profile belonging to the currently authenticated user.
     *
     * @param authentication current security principal
     * @return 200 OK with {@link ProfileResponse}
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        ProfileResponse response = profileService.getProfile(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates fitness attributes (height, weight, goalWeight, weeklyTarget) for the current user.
     *
     * @param request        profile metrics payload
     * @param authentication current security principal
     * @return 200 OK with updated {@link ProfileResponse}
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @RequestBody ProfileRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        ProfileResponse response = profileService.updateProfile(request, email);
        return ResponseEntity.ok(response);
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
