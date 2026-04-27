package com.pm.auth_service.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.pm.auth_service.dto.LoginRequestDTO;
import com.pm.auth_service.dto.LoginResponseDTO;
import com.pm.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
// @Tag(name = "Login", description = "API for logging in") // tag controller
// for open api
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Generate token on user login")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        // calls service layer of auth-service - business logic to authenticate the user
        // and send the token
        // optional object - empty or contains a string
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // get token from the Optional type
        String token = tokenOptional.get();
        // return the LoginResponseDTO as part of the HTTP response
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // when a request is sent, we add the token in the request header as the
    // authorization
    // so the format is Bearer <token>
    @Operation(summary = "Validate Token")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        
        // check if it exists and check if its is valid
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // validate the token, take the entire string starting from index 7, return true or false
        return authService.validateToken(authHeader.substring(7)) ?
            ResponseEntity.ok().build() :
            
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
}
