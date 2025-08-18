package com.unibague.gradework.orionprogram.services;

import com.unibague.gradework.orionprogram.model.UserDTO;

import java.util.Optional;

public interface IUserService {
    Optional<UserDTO> getUserById(String id);
}
