package com.example.demo.Controllers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Exceptions.Models.ResourceNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.UserService;
import com.example.demo.Services.Mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoritesController {

    private final UserService userService;
    private final EventMapper eventMapper;

    @Autowired
    public FavoritesController(UserService userService, EventMapper eventMapper) {
        this.userService = userService;
        this.eventMapper = eventMapper;
    }

    @GetMapping
    public ResponseEntity<?> getFavoriteEvents() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            User user = userService.getUserWithFavoriteEvents(username);

            List<EventResponseDTO> favoriteEvents = user.getEventsParticipating().stream()
                    .map(eventMapper::toResponseDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(favoriteEvents);
        } catch (ResourceNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to retrieve favorite events",
                    "Internal Server Error",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> addFavoriteEvent(@RequestParam Long eventId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            userService.addFavoriteEvent(username, eventId);

            return ResponseEntity.ok("Event added to favorites");
        } catch (ResourceNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User or Event",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to add favorite event",
                    "Internal Server Error",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @PostMapping("/remove")
    public ResponseEntity<?> removeFavoriteEvent(@RequestParam Long eventId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            userService.removeFavoriteEvent(username, eventId);

            return ResponseEntity.ok("Event removed from favorites");
        } catch (ResourceNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "User or Event",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to remove favorite event",
                    "Internal Server Error",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}