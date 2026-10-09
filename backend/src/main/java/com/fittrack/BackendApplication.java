package com.fittrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application entry point for the FitTrack Spring Boot application.
 * Configures component scanning, auto-configuration, and bootstraps the embedded container.
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args command-line arguments passed at launch
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
