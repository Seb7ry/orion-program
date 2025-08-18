package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.configuration.ServiceProperties;
import com.unibague.gradework.orionprogram.model.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Service for communicating with User Service
 * Uses externalized configuration for service URLs and timeouts
 */
@Slf4j
@Service
public class UserService implements IUserService {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    public UserService(RestTemplate restTemplate, ServiceProperties serviceProperties) {
        this.restTemplate = restTemplate;
        this.serviceProperties = serviceProperties;
        log.info("UserService initialized with URL: {}", serviceProperties.getUserServiceUrl());
    }

    @Override
    public Optional<UserDTO> getUserById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("The user id cannot be null or empty");
        }

        String url = serviceProperties.getUserServiceUrl() + "/" + id;
        log.debug("Fetching user by ID: {} from URL: {}", id, url);

        try {
            Object response = restTemplate
                    .getForEntity(url, Object.class)
                    .getBody();

            if (response instanceof LinkedHashMap<?, ?> userMap) {
                String idUser = (String) userMap.get("idUser");
                String name   = (String) userMap.get("name");
                String email  = (String) userMap.get("email");
                String phone  = (String) userMap.get("phone");

                if (idUser == null || idUser.isBlank()) {
                    throw new IllegalArgumentException("The user id cannot be null or empty");
                }

                UserDTO dto = UserDTO.builder()
                        .idUser(idUser)
                        .name(name)
                        .email(email)
                        .phone(phone)
                        .build();

                log.debug("Successfully retrieved user: {}", idUser);
                return Optional.of(dto);
            }

            log.warn("Unexpected response format from User Service for ID: {}", id);
            return Optional.empty();

        } catch (HttpClientErrorException.NotFound e) {
            log.debug("User not found with ID: {}", id);
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while fetching user {}: {} - {}", id, e.getStatusCode(), e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error fetching user with ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }
}