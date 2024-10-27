package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Models.TicketPrices;
import com.example.demo.Services.DBServices.CategoryService;
import com.example.demo.Services.DBServices.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {

    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public EventMapper(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public Event toEntity(EventRegisterDTO eventRegisterDTO) {
        Event event = new Event();

        // Setare câmpuri obligatorii
        event.setTitle(eventRegisterDTO.getTitle());
        event.setDescription(eventRegisterDTO.getDescription());
        event.setLocation(eventRegisterDTO.getLocation());
        event.setAddress(eventRegisterDTO.getAddress());
        event.setDate(eventRegisterDTO.getDate());
        event.setGenre(eventRegisterDTO.getGenre());

        // Setare câmpuri opționale
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

        // Setare relații Many-to-One
        User organizer = userService.getUserById(eventRegisterDTO.getOrganizerId());
        event.setCreatedBy(organizer);

        Category category = categoryService.getCategoryById(eventRegisterDTO.getCategoryId());
        event.setCategory(category);

        return event;
    }

    public EventResponseDTO toResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();

        // Setare câmpuri obligatorii
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setAddress(event.getAddress());
        dto.setDate(event.getDate());
        dto.setGenre(event.getGenre());

        // Setare câmpuri opționale
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

        // Setare informații despre creat și actualizat
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        // Setare informații despre relații Many-to-One
        dto.setOrganizerId(event.getCreatedBy().getId());
        dto.setOrganizerUsername(event.getCreatedBy().getUsername());

        dto.setCategoryId(event.getCategory().getId());
        dto.setCategoryName(event.getCategory().getName());

        return dto;
    }
}
