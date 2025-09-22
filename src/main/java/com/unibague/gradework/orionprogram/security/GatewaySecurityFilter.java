package com.unibague.gradework.orionprogram.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class GatewaySecurityFilter extends OncePerRequestFilter {

    private static final String H_GATEWAY_VALIDATED = "X-Gateway-Validated";
    private static final String H_INTERNAL_REQUEST  = "X-Internal-Request";
    private static final String H_SERVICE_REQUEST   = "X-Service-Request";
    private static final String H_SERVICE_NAME      = "X-Service-Name";
    private static final String H_SERVICE_TOKEN     = "X-Service-Token";

    @Value("${gateway.service.token:${GATEWAY_SERVICE_TOKEN:dev-token-unibague-orion}}")
    private String gatewayServiceToken;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        final String path = req.getRequestURI();

        // 0) Públicos (health)
        if (path.startsWith("/actuator") || path.equals("/health")) {
            chain.doFilter(req, res);
            return;
        }

        // 1) Si viene del gateway o marcado como interno → deja pasar
        String gv = req.getHeader(H_GATEWAY_VALIDATED);
        String ir = req.getHeader(H_INTERNAL_REQUEST);
        if ("true".equalsIgnoreCase(gv) || "true".equalsIgnoreCase(ir)) {
            chain.doFilter(req, res);
            return;
        }

        // 2) Si es S2S con token válido → deja pasar
        String sr = req.getHeader(H_SERVICE_REQUEST);
        String st = req.getHeader(H_SERVICE_TOKEN);
        String sn = req.getHeader(H_SERVICE_NAME);

        if ("true".equalsIgnoreCase(sr) && st != null && st.equals(gatewayServiceToken)) {
            log.debug("S2S allowed from {}", sn);
            chain.doFilter(req, res);
            return;
        }

        // 3) Todo lo demás bloqueado (evita acceso directo por fuera del gateway)
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json");
        res.getWriter().write(("""
            {"error":"DIRECT_ACCESS_FORBIDDEN",
             "message":"Use el API Gateway",
             "path":"%s","status":403,"service":"orion-program"}""").formatted(path));
    }
}
