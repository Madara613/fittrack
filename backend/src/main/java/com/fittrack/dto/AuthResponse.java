package com.fittrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object returned upon successful registration or login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Signed JWT bearer token string.
     */
    private String token;

    /**
     * Token scheme type, defaults to "Bearer".
     */
    @Builder.Default
    private String type = "Bearer";

    /**
     * User's unique identifier.
     */
    private Long userId;

    /**
     * User's full name.
     */
    private String name;

    /**
     * User's registered email address.
     */
    private String email;
}
