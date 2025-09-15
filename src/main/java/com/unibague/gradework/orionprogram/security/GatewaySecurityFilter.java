package com.unibague.gradework.orionprogram.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security filter to ensure requests come through the API Gateway
 * Blocks direct access to microservice endpoints but allows inter-service communication
 */
@Slf4j
@Component
@Order(1) // High priority - execute before other filters
public class GatewaySecurityFilter implements Filter {

    private static final String GATEWAY_HEADER = "X-Gateway-Validated";
    private static final String EXPECTED_VALUE = "true";
    private static final String SERVICE_HEADER = "X-Service-Request";
    private static final String INTERNAL_REQUEST_HEADER = "X-Internal-Request";

    // Public endpoints that don't require gateway validation
    private static final String[] PUBLIC_PATHS = {
            "/actuator/health",
            "/health"
    };

    // Internal service endpoints that allow direct inter-service communication
    private static final String[] INTERNAL_SERVICE_PATHS = {
            "/service/program/name/",     // Para búsqueda de programas por name
            "/service/program/",          // Para operaciones CRUD de programas
            "/service/program"            // Para crear programas (POST sin trailing slash)
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String userAgent = httpRequest.getHeader("User-Agent");

        // Allow public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Allowing public endpoint: {} {}", method, requestPath);
            chain.doFilter(request, response);
            return;
        }

        // Allow internal service communication
        if (isInternalServiceEndpoint(requestPath)) {
            log.debug("Allowing internal service communication: {} {}", method, requestPath);
            // Add service communication header for tracking
            httpRequest.setAttribute("X-Internal-Service-Request", "true");
            chain.doFilter(request, response);
            return;
        }

        // Check if it's a request from Gateway with X-Internal-Request header
        String internalRequest = httpRequest.getHeader(INTERNAL_REQUEST_HEADER);
        if ("true".equals(internalRequest)) {
            log.debug("Allowing gateway internal request: {} {} from gateway", method, requestPath);
            chain.doFilter(request, response);
            return;
        }

        // Check if it's a direct service-to-service call (Java HTTP client)
        if (isServiceToServiceCall(httpRequest)) {
            log.debug("Allowing service-to-service communication: {} {} from {}",
                    method, requestPath, userAgent);
            chain.doFilter(request, response);
            return;
        }

        // Check for gateway header for regular API calls
        String gatewayHeader = httpRequest.getHeader(GATEWAY_HEADER);

        if (!EXPECTED_VALUE.equals(gatewayHeader)) {
            log.warn("BLOCKED: Direct access attempt to {} {} - Missing or invalid gateway header: {} - User-Agent: {}",
                    method, requestPath, gatewayHeader, userAgent);

            sendUnauthorizedResponse(httpResponse, requestPath);
            return;
        }

        // Extract and log user context from gateway
        String userId = httpRequest.getHeader("X-User-ID");
        String userRole = httpRequest.getHeader("X-User-Role");

        if (userId != null) {
            log.debug("Gateway validated request: {} {} - User: {} ({})",
                    method, requestPath, userId, userRole);
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Check if endpoint is public and doesn't require gateway validation
     */
    private boolean isPublicEndpoint(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if endpoint is an internal service endpoint that allows direct communication
     */
    private boolean isInternalServiceEndpoint(String path) {
        // Special case for exact match on /service/program for POST requests
        if (path.equals("/service/program")) {
            return true;
        }

        for (String internalPath : INTERNAL_SERVICE_PATHS) {
            if (path.startsWith(internalPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the request is coming from another service (not a browser/client)
     */
    private boolean isServiceToServiceCall(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        // Check for typical service-to-service indicators
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            // Java HTTP clients, RestTemplate, etc.
            if (userAgent.contains("java") ||
                    userAgent.contains("apache-httpclient") ||
                    userAgent.contains("okhttp") ||
                    userAgent.startsWith("java/") ||
                    userAgent.contains("orion-auth-service")) {
                return true;
            }
        }

        // Check for service-specific headers
        String serviceHeader = request.getHeader(SERVICE_HEADER);
        if ("true".equals(serviceHeader)) {
            return true;
        }

        return false;
    }

    /**
     * Send unauthorized response for blocked requests
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{" +
                        "\"error\":\"DIRECT_ACCESS_FORBIDDEN\"," +
                        "\"message\":\"Direct access to microservices is not allowed. Use the API Gateway.\"," +
                        "\"path\":\"%s\"," +
                        "\"timestamp\":\"%s\"," +
                        "\"status\":403," +
                        "\"service\":\"orion-program\"," +
                        "\"suggestion\":\"Use /api/programs/ endpoints through the gateway\"" +
                        "}",
                path,
                java.time.LocalDateTime.now()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}