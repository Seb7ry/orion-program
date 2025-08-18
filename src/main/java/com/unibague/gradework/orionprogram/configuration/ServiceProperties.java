package com.unibague.gradework.orionprogram.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;

/**
 * Configuration properties for external service URLs and timeouts
 * Centralizes all service-related configuration
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "orion.services")
public class ServiceProperties {

    /**
     * User Service configuration
     */
    @NotBlank(message = "User service URL is required")
    @Pattern(regexp = "^https?://.*", message = "User service URL must be a valid HTTP/HTTPS URL")
    private String userServiceUrl = "http://localhost:8092/service/user";

    /**
     * HTTP timeout configuration
     */
    private Duration connectionTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(10);

    /**
     * Retry configuration
     */
    private int maxRetries = 3;
    private Duration retryDelay = Duration.ofMillis(500);

    /**
     * Circuit breaker configuration
     */
    private double failureRateThreshold = 50.0;
    private int minimumNumberOfCalls = 5;
    private Duration waitDurationInOpenState = Duration.ofSeconds(30);
}