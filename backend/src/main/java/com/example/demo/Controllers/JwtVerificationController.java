package com.example.demo.Controllers;

import com.example.demo.Models.ErrorResponse;
import com.example.demo.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/verifyJWT")
public class JwtVerificationController {
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtVerificationController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/isValid")
    public ResponseEntity<?> IsTokenValid(@RequestBody String token) {
        String cleanedToken = token.trim();
        if (cleanedToken.startsWith("\"") && cleanedToken.endsWith("\"")) {
            cleanedToken = cleanedToken.substring(1, cleanedToken.length() - 1);
        }

        boolean isValid = jwtTokenProvider.isTokenValid(cleanedToken);

        if(isValid){
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "You cannot access the page unless you login.",
                "Authorization",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
