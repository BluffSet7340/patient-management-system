package com.pm.auth_service.dto;

public class LoginResponseDTO {
    private final String token; // prevents it from being modified once created

    public String getToken() {
        return token;
    }

    // public void setToken(String token) {
    //     this.token = token;
    // }

    // removes the need for having a getter and setter?
    public LoginResponseDTO(String token) {
        this.token = token;
    }


}
