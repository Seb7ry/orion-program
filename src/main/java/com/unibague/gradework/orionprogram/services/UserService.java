package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final RestTemplate restTemplate;

    @Value("${users.service.url}")
    private String serviceUrl;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<UserDTO> getUserById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("The user id cannot be null or empty");
        }

        try {
            Object response = restTemplate
                    .getForEntity(serviceUrl + "/" + id, Object.class)
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

                return Optional.of(dto);
            }

            // Respuesta inesperada
            return Optional.empty();

        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("Error al obtener el usuario con ID " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}