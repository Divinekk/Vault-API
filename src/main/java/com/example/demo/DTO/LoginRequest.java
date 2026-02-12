package com.example.demo.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.aspectj.bridge.Message;

@Data


public class LoginRequest {
    @NotBlank (message = "Email is required")
    @Email (message = "Email must be valid")
    private String email;

    @NotBlank (message = "Password is required")
    private String passwordHash;
}
