package com.example.demo.Services.Customs;

import com.example.demo.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtVerificationService {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtVerificationService(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public boolean IsTokenValid(String token){
        return jwtTokenProvider.isTokenValid(token);
    }
}
