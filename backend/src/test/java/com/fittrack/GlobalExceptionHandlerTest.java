package com.fittrack;

import com.fittrack.dto.ErrorResponse;
import com.fittrack.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        request = mockRequest;
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Workout not found with id: 123");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(404, body.getStatus());
            assertEquals("Not Found", body.getError());
            assertEquals("Workout not found with id: 123", body.getMessage());
            assertEquals("/api/test", body.getPath());
            assertNotNull(body.getTimestamp());
        }
    }

    @Test
    void testHandleBadRequestException() {
        BadRequestException ex = new BadRequestException("Workout type is required");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequestException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(400, body.getStatus());
            assertEquals("Bad Request", body.getError());
            assertEquals("Workout type is required", body.getMessage());
            assertEquals("/api/test", body.getPath());
        }
    }

    @Test
    void testHandleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("User is not authenticated");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(401, body.getStatus());
            assertEquals("Unauthorized", body.getError());
            assertEquals("User is not authenticated", body.getMessage());
        }
    }

    @Test
    void testHandleForbiddenException() {
        ForbiddenException ex = new ForbiddenException("You are not authorized to modify this workout");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleForbiddenException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(403, body.getStatus());
            assertEquals("Forbidden", body.getError());
            assertEquals("You are not authorized to modify this workout", body.getMessage());
        }
    }

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "Already exists");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResponseStatusException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(409, body.getStatus());
            assertEquals("Already exists", body.getMessage());
        }
    }

    @Test
    void testHandleBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(401, body.getStatus());
            assertEquals("Invalid email or password", body.getMessage());
        }
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(403, body.getStatus());
            assertEquals("Access denied", body.getMessage());
        }
    }

    @Test
    void testHandleGenericException() {
        RuntimeException ex = new RuntimeException("Unexpected error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        if (body != null) {
            assertEquals(500, body.getStatus());
            assertTrue(body.getMessage().contains("Unexpected error"));
        }
    }
}
