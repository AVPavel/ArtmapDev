package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.News;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.News.NewsRegisterDTO;
import com.example.demo.Exceptions.Models.NewsNotFoundException;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.Mappers.NewsMapper;
import com.example.demo.Repositories.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing News entities.
 */
@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;  // Add these
    private final EventRepository eventRepository;
    private final NewsMapper newsMapper;

    public NewsService(NewsRepository newsRepository,
                       UserRepository userRepository,
                       EventRepository eventRepository,
                       NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.newsMapper = newsMapper;
    }

    /**
     * Creates a new News entity based on the provided DTO.
     *
     * @param dto The DTO containing news data.
     * @return The created News entity.
     */
    @Transactional
    public News createNews(NewsRegisterDTO dto) {
        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        News news = newsMapper.toEntity(dto);
        news.setCreatedBy(creator);
        news.setEvent(event);

        return newsRepository.save(news);
    }

    /**
     * Retrieves a News entity by its ID.
     *
     * @param id The ID of the News entity.
     * @return The News entity.
     * @throws NewsNotFoundException If the News entity is not found.
     */
    @Transactional(readOnly = true)
    public News getNewsById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("Could not find news with id: " + id));
    }

    /**
     * Retrieves all News entities.
     *
     * @return A list of all News entities.
     */
    @Transactional(readOnly = true)
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    /**
     * Updates an existing News entity with data from the provided DTO.
     *
     * @param id  The ID of the News entity to update.
     * @param dto The DTO containing updated news data.
     * @return The updated News entity.
     * @throws NewsNotFoundException If the News entity is not found.
     */
    @Transactional
    public News updateNews(Long id, NewsRegisterDTO dto) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("Could not find news with id: " + id));

        // Handle user/event updates
        if(!dto.getCreatorId().equals(existingNews.getCreatedBy().getId())) {
            User newCreator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(() -> new IllegalArgumentException("New user not found"));
            existingNews.setCreatedBy(newCreator);
        }

        if(!dto.getEventId().equals(existingNews.getEvent().getId())) {
            Event newEvent = eventRepository.findById(dto.getEventId())
                    .orElseThrow(() -> new IllegalArgumentException("New event not found"));
            existingNews.setEvent(newEvent);
        }

        newsMapper.updateEntityFromDTO(dto, existingNews);
        return newsRepository.save(existingNews);
    }

    /**
     * Deletes a News entity by its ID.
     *
     * @param id The ID of the News entity to delete.
     * @throws NewsNotFoundException If the News entity is not found.
     */
    @Transactional
    public void deleteNews(Long id) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("Could not find news with id: " + id));
        newsRepository.delete(existingNews);
    }
}
