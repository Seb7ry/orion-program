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
 * Blocks direct access to microservice endpoints
 */
@Slf4j
@Component
@Order(1) // High priority - execute before other filters
public class GatewaySecurityFilter implements Filter {

    private static final String GATEWAY_HEADER = "X-Gateway-Validated";
    private static final String EXPECTED_VALUE = "true";

    // Public endpoints that don't require gateway validation
    private static final String[] PUBLIC_PATHS = {
            "/actuator/health",
            "/health"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Allow public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Allowing public endpoint: {} {}", method, requestPath);
            chain.doFilter(request, response);
            return;
        }

        // Check for gateway header
        String gatewayHeader = httpRequest.getHeader(GATEWAY_HEADER);

        if (!EXPECTED_VALUE.equals(gatewayHeader)) {
            log.warn("BLOCKED: Direct access attempt to {} {} - Missing or invalid gateway header: {}",
                    method, requestPath, gatewayHeader);

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
                        "\"service\":\"orion-program\"" +
                        "}",
                path,
                java.time.LocalDateTime.now()
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}