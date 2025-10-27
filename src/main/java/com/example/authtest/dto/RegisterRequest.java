package com.example.authtest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank @Email
    public String email;
    @NotBlank @Size(min = 3, max = 50)
    public String name;
    @NotBlank @Size(min = 6, max = 100)
    public String password;
}
