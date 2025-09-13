package com.unibague.gradework.orionprogram.security;

import lombok.Data;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for accessing user context from gateway headers
 * Provides authenticated user information to controllers and services
 */
@Slf4j
public class UserContext {

    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String USER_ROLE_HEADER = "X-User-Role";
    private static final String USER_PROGRAMS_HEADER = "X-User-Programs";
    private static final String GATEWAY_VALIDATED_HEADER = "X-Gateway-Validated";

    /**
     * Data class representing authenticated user information
     */
    @Data
    @Builder
    public static class AuthenticatedUser {
        private String userId;
        private String role;
        private List<String> programs;
        private boolean gatewayValidated;

        /**
         * Check if user has a specific role
         */
        public boolean hasRole(String role) {
            return this.role != null && this.role.equalsIgnoreCase(role);
        }

        /**
         * Check if user has access to a specific program
         */
        public boolean hasAccessToProgram(String programId) {
            return programs != null && programs.contains(programId);
        }

        /**
         * Check if user is an admin
         */
        public boolean isAdmin() {
            return hasRole("ADMIN") || hasRole("ADMINISTRATOR");
        }

        /**
         * Check if user is a coordinator
         */
        public boolean isCoordinator() {
            return hasRole("COORDINATOR");
        }

        /**
         * Check if user is a student
         */
        public boolean isStudent() {
            return hasRole("STUDENT") || hasRole("ESTUDIANTE");
        }

        /**
         * Check if user is a teacher/actor
         */
        public boolean isTeacher() {
            return hasRole("TEACHER") || hasRole("DOCENTE") || hasRole("ACTOR");
        }
    }

    /**
     * Get current authenticated user from request headers
     */
    public static Optional<AuthenticatedUser> getCurrentUser() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                log.debug("No request attributes available");
                return Optional.empty();
            }

            HttpServletRequest request = attributes.getRequest();

            // Check if request is validated by gateway
            String gatewayValidated = request.getHeader(GATEWAY_VALIDATED_HEADER);
            if (!"true".equals(gatewayValidated)) {
                log.debug("Request not validated by gateway");
                return Optional.empty();
            }

            String userId = request.getHeader(USER_ID_HEADER);
            String role = request.getHeader(USER_ROLE_HEADER);
            String programsHeader = request.getHeader(USER_PROGRAMS_HEADER);

            if (userId == null || userId.trim().isEmpty()) {
                log.debug("No user ID in headers");
                return Optional.empty();
            }

            // Parse programs list
            List<String> programs = null;
            if (programsHeader != null && !programsHeader.trim().isEmpty()) {
                programs = Arrays.asList(programsHeader.split(","));
            }

            AuthenticatedUser user = AuthenticatedUser.builder()
                    .userId(userId.trim())
                    .role(role != null ? role.trim() : null)
                    .programs(programs)
                    .gatewayValidated(true)
                    .build();

            log.debug("Retrieved user context: {} ({})", user.getUserId(), user.getRole());
            return Optional.of(user);

        } catch (Exception e) {
            log.warn("Error retrieving user context: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get current user ID or throw exception if not authenticated
     */
    public static String getCurrentUserId() {
        return getCurrentUser()
                .map(AuthenticatedUser::getUserId)
                .orElseThrow(() -> new SecurityException("No authenticated user found"));
    }

    /**
     * Get current user role or return null if not authenticated
     */
    public static String getCurrentUserRole() {
        return getCurrentUser()
                .map(AuthenticatedUser::getRole)
                .orElse(null);
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    /**
     * Check if current user has access to a specific program
     */
    public static boolean hasAccessToProgram(String programId) {
        return getCurrentUser()
                .map(user -> user.hasAccessToProgram(programId))
                .orElse(false);
    }

    /**
     * Require authentication - throw exception if user is not authenticated
     */
    public static AuthenticatedUser requireAuthentication() {
        return getCurrentUser()
                .orElseThrow(() -> new SecurityException("Authentication required"));
    }

    /**
     * Require specific role - throw exception if user doesn't have the role
     */
    public static void requireRole(String role) {
        AuthenticatedUser user = requireAuthentication();
        if (!user.hasRole(role)) {
            throw new SecurityException("Role '" + role + "' required. User has role: " + user.getRole());
        }
    }

    /**
     * Require admin role
     */
    public static void requireAdmin() {
        AuthenticatedUser user = requireAuthentication();
        if (!user.isAdmin()) {
            throw new SecurityException("Administrator privileges required");
        }
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentUser().isPresent();
    }
}