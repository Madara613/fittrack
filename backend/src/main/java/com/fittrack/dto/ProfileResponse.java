package com.fittrack.dto;

import com.fittrack.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object returning profile data to client callers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    /**
     * Unique profile ID.
     */
    private Long id;

    /**
     * User ID associated with this profile.
     */
    private Long userId;

    /**
     * User's display name.
     */
    private String name;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's recorded height in cm.
     */
    private Double height;

    /**
     * User's recorded weight in kg.
     */
    private Double weight;

    /**
     * User's goal weight in kg.
     */
    private Double goalWeight;

    /**
     * Target number of workouts per week.
     */
    private Integer weeklyTarget;

    /**
     * Maps a {@link Profile} entity into a clean {@link ProfileResponse} DTO.
     *
     * @param profile entity instance
     * @return populated DTO or null if entity is null
     */
    public static ProfileResponse fromEntity(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .name(profile.getUser() != null ? profile.getUser().getName() : null)
                .email(profile.getUser() != null ? profile.getUser().getEmail() : null)
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .goalWeight(profile.getGoalWeight())
                .weeklyTarget(profile.getWeeklyTarget())
                .build();
    }
}
