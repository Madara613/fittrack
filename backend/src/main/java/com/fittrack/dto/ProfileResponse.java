package com.fittrack.dto;

import com.fittrack.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Long id;

    private Long userId;

    private String name;

    private String email;

    private Double height;

    private Double weight;

    private Double goalWeight;

    private Integer weeklyTarget;

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
