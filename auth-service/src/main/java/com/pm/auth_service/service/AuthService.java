package com.pm.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.model.User;

@Service // belongs to service layer and handles business logic
public class AuthService {
    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        // first grab user from the db via email
        Optional<User> user = UserService.findByEmail(loginRequestDTO.getEmail());
    }
}
