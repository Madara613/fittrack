package com.fittrack.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard data transfer object for structured, clean API error responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code (e.g. 400, 401, 403, 404, 500).
     */
    private int status;

    /**
     * Standard HTTP status reason phrase (e.g. "Not Found", "Bad Request").
     */
    private String error;

    /**
     * Clear, human-readable error description.
     */
    private String message;

    /**
     * The URI path of the request where the error occurred.
     */
    private String path;

    /**
     * Timestamp representing when the error occurred.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Specific field-level validation error messages (if applicable).
     */
    private Map<String, String> validationErrors;
}
