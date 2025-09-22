package com.unibague.gradework.orionprogram.configuration;

import com.unibague.gradework.orionprogram.security.GatewaySecurityFilter;
import com.unibague.gradework.orionprogram.security.UserContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filtro que puebla el UserContext desde headers en cada request.
     * Se ejecuta DESPUÉS del GatewaySecurityFilter.
     */
    @Bean
    public Filter userContextPopulateFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    jakarta.servlet.http.HttpServletResponse response,
                    FilterChain filterChain
            ) throws ServletException, IOException {
                try {
                    UserContext.populateFrom(request);
                    filterChain.doFilter(request, response);
                } finally {
                    UserContext.clear();
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            GatewaySecurityFilter gatewaySecurityFilter,
            Filter userContextPopulateFilter
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            // Primero valida que venga del gateway / interno
            .addFilterBefore(gatewaySecurityFilter, BasicAuthenticationFilter.class)
            // Luego construye el UserContext (inyecta SYSTEM admin si es S2S)
            .addFilterAfter(userContextPopulateFilter, GatewaySecurityFilter.class);

        return http.build();
    }
}
