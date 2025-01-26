package com.unibague.gradework.orionprogram.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 *
 * This class defines the security settings for the application, including authorization
 * and CSRF protection. By default, it permits all requests and disables CSRF protection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     *
     * - All incoming requests are permitted without authentication.
     * - Cross-Site Request Forgery (CSRF) protection is disabled.
     *
     * @param http the {@link HttpSecurity} object used to configure security settings.
     * @return the configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs while configuring security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
