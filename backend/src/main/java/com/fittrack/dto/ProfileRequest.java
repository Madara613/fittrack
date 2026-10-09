package com.fittrack.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object encapsulating profile update parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

    /**
     * User's height in centimeters.
     */
    private Double height;

    /**
     * User's current weight in kilograms.
     */
    private Double weight;

    /**
     * User's target goal weight in kilograms.
     */
    private Double goalWeight;

    /**
     * User's weekly workout target frequency.
     */
    private Integer weeklyTarget;
}
