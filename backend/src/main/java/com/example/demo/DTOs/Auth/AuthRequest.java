package com.example.demo.DTOs.Auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
