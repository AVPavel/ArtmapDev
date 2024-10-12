package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventMapper {

    private final UserService userService;

    //TODO:Finish CategoryService to get Categories
    /*private final CategoryService categoryService;*/

    @Autowired
    public EventMapper(UserService userService) {
        this.userService = userService;
    }

    public Event toEntity(EventRegisterDTO eventRegisterDTO) {
        Event event = new Event();

        event.setTitle(eventRegisterDTO.getTitle());
        event.setDescription(eventRegisterDTO.getDescription());
        event.setLocation(eventRegisterDTO.getLocation());
        event.setDate(eventRegisterDTO.getDate());

        // Obține organizatorul
        User organizer = userService.getUserById(eventRegisterDTO.getOrganizerId());
        event.setCreatedBy(organizer);

        //TODO: use categoryService to get cateogry
        /*Category category = categoryService.getCategoryById(eventRegisterDTO.getCategoryId())
        event.setCategory(category);*/

        return event;
    }

    public EventResponseDTO toResponseDTO(Event event) {
        EventResponseDTO dto = new EventResponseDTO();

        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setOrganizerId(event.getCreatedBy().getId());
        dto.setOrganizerUsername(event.getCreatedBy().getUsername());
        dto.setDate(event.getDate());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setCategoryName(event.getCategory().getName());

        return dto;
    }
}
