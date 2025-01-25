package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Genre;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Models.TicketPrices;
import com.example.demo.Services.DBServices.CategoryService;
import com.example.demo.Services.DBServices.GenreService;
import com.example.demo.Services.DBServices.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventMapper {

    private final UserService userService;
    private final CategoryService categoryService;
    private final GenreService genreService;

    @Autowired
    public EventMapper(UserService userService, CategoryService categoryService, GenreService genreService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.genreService = genreService;
    }

    public Event toEntity(EventRegisterDTO eventRegisterDTO) {
        Event event = new Event();

        // Set required fields
        event.setTitle(eventRegisterDTO.getTitle());
        event.setDescription(eventRegisterDTO.getDescription());
        event.setLocation(eventRegisterDTO.getLocation());
        event.setAddress(eventRegisterDTO.getAddress());
        event.setDate(eventRegisterDTO.getDate());

        // Retrieve and set genres by IDs
        Set<Genre> genres = eventRegisterDTO.getGenreId().stream()
                .map(genreService::getGenreById)
                .collect(Collectors.toSet());
        event.setGenres(genres);

        // Set optional fields
        if (eventRegisterDTO.getLatitude() != null) {
            event.setLatitude(eventRegisterDTO.getLatitude());
        }

        if (eventRegisterDTO.getLongitude() != null) {
            event.setLongitude(eventRegisterDTO.getLongitude());
        }

        if (eventRegisterDTO.getTicketPrices() != null && !eventRegisterDTO.getTicketPrices().isEmpty()) {
            try {
                TicketPrices ticketPrices = TicketPrices.fromJson(eventRegisterDTO.getTicketPrices());
                event.setTicketPrices(ticketPrices);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Invalid ticketPrices JSON format", e);
            }
        }

        if (eventRegisterDTO.getCheapestTicket() != null) {
            event.setCheapestTicket(eventRegisterDTO.getCheapestTicket());
        }

        // Set relationships
        User organizer = userService.getUserById(eventRegisterDTO.getOrganizerId());
        event.setCreatedBy(organizer);

        Category category = categoryService.getCategoryById(eventRegisterDTO.getCategoryId());
        event.setCategory(category);

        return event;
    }

    public EventResponseDTO toResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();

        // Set required fields
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setAddress(event.getAddress());
        dto.setDate(event.getDate());

        // Map genre names from the Genre entities
        Set<String> genreNames = event.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());
        dto.setGenreName(genreNames);

        // Set optional fields
        if (event.getLatitude() != null) {
            dto.setLatitude(event.getLatitude());
        }

        if (event.getLongitude() != null) {
            dto.setLongitude(event.getLongitude());
        }

        if (event.getTicketPrices() != null) {
            try {
                dto.setTicketPrices(event.getTicketPrices().toJson());
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Error converting TicketPrices to JSON", e);
            }
        }

        if (event.getCheapestTicket() != null) {
            dto.setCheapestTicket(event.getCheapestTicket());
        }

        // Set timestamps
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        // Set relationships
        dto.setOrganizerId(event.getCreatedBy().getId());
        dto.setOrganizerUsername(event.getCreatedBy().getUsername());

        dto.setCategoryId(event.getCategory().getId());
        dto.setCategoryName(event.getCategory().getName());

        return dto;
    } /**
     * Updates an existing Event entity with values from an EventRegisterDTO.
     *
     * @param eventRegisterDTO The DTO containing updated event data.
     * @param event            The existing Event entity to update.
     */
    public void updateEntityFromDTO(EventRegisterDTO eventRegisterDTO, Event event) {

        if (eventRegisterDTO.getTitle() != null) {
            event.setTitle(eventRegisterDTO.getTitle());
        }

        if (eventRegisterDTO.getDescription() != null) {
            event.setDescription(eventRegisterDTO.getDescription());
        }

        if (eventRegisterDTO.getLocation() != null) {
            event.setLocation(eventRegisterDTO.getLocation());
        }

        if (eventRegisterDTO.getAddress() != null) {
            event.setAddress(eventRegisterDTO.getAddress());
        }

        if (eventRegisterDTO.getDate() != null) {
            event.setDate(eventRegisterDTO.getDate());
        }

        if (eventRegisterDTO.getGenreId() != null && !eventRegisterDTO.getGenreId().isEmpty()) {
            Set<Genre> genres = eventRegisterDTO.getGenreId().stream()
                    .map(genreService::getGenreById)
                    .collect(Collectors.toSet());
            event.setGenres(genres);
        }

        if (eventRegisterDTO.getLatitude() != null) {
            event.setLatitude(eventRegisterDTO.getLatitude());
        }

        if (eventRegisterDTO.getLongitude() != null) {
            event.setLongitude(eventRegisterDTO.getLongitude());
        }

        if (eventRegisterDTO.getTicketPrices() != null) {
            if (!eventRegisterDTO.getTicketPrices().isEmpty()) {
                try {
                    TicketPrices ticketPrices = TicketPrices.fromJson(eventRegisterDTO.getTicketPrices());
                    event.setTicketPrices(ticketPrices);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Invalid ticketPrices JSON format", e);
                }
            } else {
                event.setTicketPrices(null);
            }
        }

        if (eventRegisterDTO.getCheapestTicket() != null) {
            event.setCheapestTicket(eventRegisterDTO.getCheapestTicket());
        }

        if (eventRegisterDTO.getOrganizerId() != null) {
            User organizer = userService.getUserById(eventRegisterDTO.getOrganizerId());
            event.setCreatedBy(organizer);
        }

        if (eventRegisterDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(eventRegisterDTO.getCategoryId());
            event.setCategory(category);
        }

        event.setUpdatedAt(java.time.LocalDateTime.now());
    }
}
