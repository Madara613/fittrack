package com.fittrack.exception;

/**
 * Exception thrown when a client request violates business constraints or validation rules.
 * Maps to HTTP 400 BAD_REQUEST.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
