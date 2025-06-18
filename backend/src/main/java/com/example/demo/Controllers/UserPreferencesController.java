package com.example.demo.Controllers;

import com.example.demo.DBModels.User;
import com.example.demo.DBModels.UserPreference;
import com.example.demo.DTOs.UserPreferences.PreferenceRequest;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.JwtTokenProvider;
import com.example.demo.Services.DBServices.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {
    private final UserPreferenceService userPreferenceService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserPreferencesController(UserPreferenceService userPreferenceService,
                                     UserRepository userRepository,
                                     AuthenticationManager authenticationManager,
                                     JwtTokenProvider jwtTokenProvider) {
        this.userPreferenceService = userPreferenceService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPreference(@RequestBody PreferenceRequest request) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Received PreferenceRequest: CategoryId=" + request.getCategoryId() + ", GenreId=" + request.getGenreId());
            if (authentication != null && authentication.isAuthenticated()) {
                System.out.println("Authenticated User: " + authentication.getName());
            } else {
                System.out.println("User not authenticated for this request.");
            }


            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserPreference preference = userPreferenceService.addUserPreference(
                    user.getId(),
                    request.getCategoryId(),
                    request.getGenreId()
            );

            return ResponseEntity.ok(preference);
        } catch (RuntimeException e) {
            System.err.println("Error adding preference: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my-preferences")
    public ResponseEntity<?> getUserPreferences() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }

            String username = authentication.getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<UserPreference> preferences = userPreferenceService.getUserPreferences(user.getId());
            return ResponseEntity.ok(preferences);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
