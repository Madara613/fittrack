package com.fittrack.exception;

/**
 * Exception thrown when an authenticated user attempts to access or modify a resource they do not own.
 * Maps to HTTP 403 FORBIDDEN.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
