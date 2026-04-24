package com.pm.auth_service.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.dto.LoginResponseDTO;
import com.pm.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
// @Tag(name = "Login", description = "API for logging in") // tag controller for open api
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // calls service layer of auth-service - business logic to authenticate the user and send the token
        // optional object - empty or contains a string
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if(tokenOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // get token from the Optional type
        String token = tokenOptional.get();
        // return the LoginResponseDTO as part of the HTTP response
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }


}
