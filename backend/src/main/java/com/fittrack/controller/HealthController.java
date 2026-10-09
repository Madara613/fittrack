package com.fittrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller providing simple liveness probes for deployment monitoring.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Verifies that the service application is responsive.
     *
     * @return "OK" string status message
     */
    @GetMapping("/health")
    public String checkHealth() {
        return "OK";
    }
}
