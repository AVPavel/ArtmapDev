package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.News;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.News.NewsRegisterDTO;
import com.example.demo.DTOs.News.NewsResponseDTO;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mapper service for converting between News entities and DTOs.
 */
@Service
public class NewsMapper {

    private final UserRepository userRepo;
    private final EventRepository eventRepo;

    @Autowired
    public NewsMapper(UserRepository ur, EventRepository er) {
        this.userRepo = ur;
        this.eventRepo = er;
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
        news.setPhoto(dto.getPhoto()); // Set the photo

        // Fetch entities directly
        news.setCreatedBy(userRepo.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid creator ID")));

        news.setEvent(eventRepo.findById(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID")));

        // Timestamps handled by entity lifecycle
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
        dto.setPhoto(news.getPhoto()); // Set the photo

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
        if (dto.getTitle() != null) news.setTitle(dto.getTitle());
        if (dto.getContent() != null) news.setContent(dto.getContent());
        if (dto.getPhoto() != null) news.setPhoto(dto.getPhoto()); // Update the photo

        // Update relationships
        if (!news.getCreatedBy().getId().equals(dto.getCreatorId())) {
            news.setCreatedBy(userRepo.findById(dto.getCreatorId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid creator ID")));
        }

        if (!news.getEvent().getId().equals(dto.getEventId())) {
            news.setEvent(eventRepo.findById(dto.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event ID")));
        }
    }
}
