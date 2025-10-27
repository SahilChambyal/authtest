package com.example.authtest.dto;

public class AuthResponse {
    public String accessToken;
    public String tokenType = "Bearer";
    public long expiresIn;

    public AuthResponse(String token, long expiresIn) {
        this.accessToken = token;
        this.expiresIn = expiresIn;
    }
}