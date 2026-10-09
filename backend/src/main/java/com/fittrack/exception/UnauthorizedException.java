package com.fittrack.exception;

/**
 * Exception thrown when authentication credentials are missing, invalid, or expired.
 * Maps to HTTP 401 UNAUTHORIZED.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
