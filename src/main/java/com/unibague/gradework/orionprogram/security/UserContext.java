package com.unibague.gradework.orionprogram.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class UserContext {

    private static final ThreadLocal<AuthenticatedUser> CTX = new ThreadLocal<>();

    // Headers que vienen del gateway cuando hay JWT válido
    public static final String H_USER_ID       = "X-User-ID";
    public static final String H_USER_EMAIL    = "X-User-Email";
    public static final String H_USER_ROLE     = "X-User-Role";
    public static final String H_USER_PROGRAMS = "X-User-Programs";

    public static final String H_INTERNAL      = "X-Internal-Request";
    public static final String H_SERVICE_REQ   = "X-Service-Request";

    // Rol del usuario de sistema
    public static final String ROLE_ADMIN = "ADMIN";

    @Getter
    @ToString
    @AllArgsConstructor
    public static class AuthenticatedUser {
        private String userId;
        private String email;
        private String role;
        private Set<String> programs;

        public boolean isAdmin() {
            return ROLE_ADMIN.equalsIgnoreCase(role);
        }

        public boolean isCoordinator() {
            return "COORDINATOR".equalsIgnoreCase(role);
        }

        public boolean hasAccessToProgram(String programId) {
            if (programs == null || programs.isEmpty()) return false;
            return programs.contains("*") || programs.contains(programId);
        }
    }

    /**
     * Pobla el contexto por request.
     * Si es interno (X-Internal-Request=true o X-Service-Request=true) y NO hay usuario,
     * se inyecta un usuario de sistema con rol ADMIN y acceso total.
     */
    public static void populateFrom(HttpServletRequest req) {
        if (CTX.get() != null) return; // ya poblado

        String userId = header(req, H_USER_ID);
        String email  = header(req, H_USER_EMAIL);
        String role   = header(req, H_USER_ROLE);
        String progs  = header(req, H_USER_PROGRAMS);

        boolean isInternal = "true".equalsIgnoreCase(header(req, H_INTERNAL))
                || "true".equalsIgnoreCase(header(req, H_SERVICE_REQ));

        if (userId == null && isInternal) {
            AuthenticatedUser sys = new AuthenticatedUser(
                    "system",
                    "system@local",
                    ROLE_ADMIN,
                    Set.of("*")
            );
            CTX.set(sys);
            log.debug("Injected SYSTEM user for internal request");
            return;
        }

        if (userId == null) {
            log.debug("No user ID in headers");
            return;
        }

        Set<String> programs = new HashSet<>();
        if (progs != null && !progs.isBlank()) {
            Arrays.stream(progs.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .forEach(programs::add);
        }

        AuthenticatedUser au = new AuthenticatedUser(
                userId,
                email,
                role != null ? role : "",
                programs
        );
        CTX.set(au);
    }

    public static Optional<AuthenticatedUser> getCurrentUser() {
        return Optional.ofNullable(CTX.get());
    }

    public static AuthenticatedUser requireAuthentication() {
        AuthenticatedUser u = CTX.get();
        if (u == null) throw new SecurityException("Authentication required");
        return u;
    }

    public static void requireAdmin() {
        AuthenticatedUser u = requireAuthentication();
        if (!u.isAdmin()) throw new SecurityException("Admin privileges required");
    }

    public static void clear() {
        CTX.remove();
    }

    private static String header(HttpServletRequest req, String name) {
        String v = req.getHeader(name);
        return (v == null || v.isBlank()) ? null : v;
    }
}
