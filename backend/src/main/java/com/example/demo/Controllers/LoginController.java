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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

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
    // LoginController.java
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        try {
            String token = payload.get("token");
            GoogleIdToken.Payload googleUser = verifyGoogleToken(token);

            // Check if user exists in your system
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(googleUser.getEmail());

            // If user doesn't exist, create new user
            if (userDetails == null) {
                userDetails = createNewUserFromGoogle(googleUser);
            }

            String jwt = jwtTokenProvider.generateToken(userDetails.getUsername());
            // ... rest of JWT response code ...

            return ResponseEntity.ok(new AuthResponse(...));
        } catch (Exception e) {
            // Handle errors
        }
    }

    private GoogleIdToken.Payload verifyGoogleToken(String token) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("YOUR_GOOGLE_CLIENT_ID"))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            throw new RuntimeException("Invalid Google token");
        }
        return idToken.getPayload();
    }
    private UserDetails createNewUserFromGoogle(GoogleIdToken.Payload googleUser) {
        User newUser = new User();
        newUser.setEmail(googleUser.getEmail());
        newUser.setUsername(googleUser.getEmail());
        // Set other fields as needed
        return customUserDetailsService.createUser(newUser);
    }
}
