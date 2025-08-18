package com.unibague.gradework.orionprogram.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for RestTemplate with externalized timeouts
 * Provides configured HTTP client for external service calls
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    private final ServiceProperties serviceProperties;

    public RestTemplateConfig(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    /**
     * Creates RestTemplate with configured timeouts and error handling
     * @return configured RestTemplate bean
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Configuring RestTemplate with connection timeout: {} and read timeout: {}",
                serviceProperties.getConnectionTimeout(),
                serviceProperties.getReadTimeout());

        return builder
                .setConnectTimeout(serviceProperties.getConnectionTimeout())
                .setReadTimeout(serviceProperties.getReadTimeout())
                .build();
    }
}