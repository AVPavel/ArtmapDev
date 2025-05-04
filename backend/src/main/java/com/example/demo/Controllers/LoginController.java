package com.example.demo.Controllers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Auth.AuthRequest;
import com.example.demo.DTOs.Auth.AuthResponse;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Security.JwtTokenProvider;
import com.example.demo.Services.Customs.CustomUserDetailsService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${google.client-id}")
    private String googleClientId;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(userDetails.getUsername());
            long expiration = jwtTokenProvider.getExpirationFromToken(jwt);
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("USER");

            return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(),role,expiration));

        } catch (AuthenticationException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    401,
                    "Invalid username or password",
                    "Authentication",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            GoogleIdToken.Payload googleUser = verifyGoogleToken(token);

            if (!googleUser.getEmailVerified()) {
                throw new RuntimeException("Email not verified by Google");
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(googleUser.getEmail());

            // If user doesn't exist, create new user
            if (userDetails == null) {
                userDetails = customUserDetailsService.createUserFromGoogle(googleUser);
            }

            String jwt = jwtTokenProvider.generateToken(userDetails.getUsername());
            long expiration = jwtTokenProvider.getExpirationFromToken(jwt);
            String role = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("USER");

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    userDetails.getUsername(),
                    role,
                    expiration
            ));

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    401,
                    "Google authentication failed: " + e.getMessage(),
                    "Google Authentication",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    private GoogleIdToken.Payload verifyGoogleToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            throw new RuntimeException("Invalid Google token");
        }
        return idToken.getPayload();
    }
}
