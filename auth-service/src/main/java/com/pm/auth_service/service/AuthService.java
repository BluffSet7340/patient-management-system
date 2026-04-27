package com.pm.auth_service.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.util.JwtUtil;

import io.jsonwebtoken.JwtException;

@Service // belongs to service layer and handles business logic
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // dependency injection
    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        // first grab user from the db via email
        // UserService is extensible, can have all the business logic and easily
        // accessible
        Optional<String> token = userService.findByEmail(loginRequestDTO.getEmail())
                // the us variable is the result of the call of the findByEmail
                // passwordEncoder checks if the hashes match
                .filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword())) // every request
                                                                                                      // passes through
                                                                                                      // the filter
                                                                                                      // chain
                // if match is successful it passes the user down the chain into the map
                // function
                // and uses jwtUtil to generate a token using the email and role
                .map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));

        return token;
    }

    // validation should also include authenticate too?
    public Boolean validateToken(String authToken) {
        try {
            jwtUtil.validateToken(authToken);
            return true;
            // one catch block required to handle the exceptions from the validateToken method
        } catch (JwtException e) { // invalid token causes JwtException
            return false;
        }
    }
}
