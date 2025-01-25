package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.News;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.News.NewsRegisterDTO;
import com.example.demo.DTOs.News.NewsResponseDTO;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.DBServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Mapper service for converting between News entities and DTOs.
 */
@Service
public class NewsMapper {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public NewsMapper(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    /**
     * Converts a NewsRegisterDTO to a new News entity.
     *
     * @param dto The DTO containing news data.
     * @return A new News entity populated with data from the DTO.
     */
    public News toEntity(NewsRegisterDTO dto) {
        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());

        // Set relationships
        User creator = userService.getUserById(dto.getCreatorId());
        news.setCreatedBy(creator);

        if (dto.getEventId() != null) {
            Event event = eventService.getEventById(dto.getEventId());
            news.setEvent(event);
        }

        // Set timestamps
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());

        return news;
    }

    /**
     * Converts a News entity to a NewsResponseDTO.
     *
     * @param news The News entity to convert.
     * @return A NewsResponseDTO populated with data from the entity.
     */
    public NewsResponseDTO toResponseDTO(News news) {
        NewsResponseDTO dto = new NewsResponseDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());

        // Set relationships
        dto.setCreatorId(news.getCreatedBy().getId());
        dto.setCreatorUsername(news.getCreatedBy().getUsername());

        if (news.getEvent() != null) {
            dto.setEventId(news.getEvent().getId());
            dto.setEventTitle(news.getEvent().getTitle());
        }

        // Set timestamps
        dto.setCreatedAt(news.getCreatedAt());
        dto.setUpdatedAt(news.getUpdatedAt());

        return dto;
    }

    /**
     * Updates an existing News entity with values from a NewsRegisterDTO.
     *
     * @param dto   The DTO containing updated news data.
     * @param news  The existing News entity to update.
     */
    public void updateEntityFromDTO(NewsRegisterDTO dto, News news) {
        if (dto.getTitle() != null) {
            news.setTitle(dto.getTitle());
        }

        if (dto.getContent() != null) {
            news.setContent(dto.getContent());
        }

        // Update relationships if provided
        if (dto.getCreatorId() != null) {
            User creator = userService.getUserById(dto.getCreatorId());
            news.setCreatedBy(creator);
        }

        if (dto.getEventId() != null) {
            Event event = eventService.getEventById(dto.getEventId());
            news.setEvent(event);
        }

        // Update the updatedAt timestamp
        news.setUpdatedAt(LocalDateTime.now());
    }
}
