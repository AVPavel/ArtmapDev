package com.example.demo.Controllers;

import com.example.demo.DBModels.News;
import com.example.demo.DTOs.News.NewsRegisterDTO;
import com.example.demo.DTOs.News.NewsResponseDTO;
import com.example.demo.Exceptions.Models.NewsNotFoundException;
import com.example.demo.Services.Mappers.NewsMapper;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.NewsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private final static Logger LOGGER = LoggerFactory.getLogger(NewsController.class);
    private final NewsService newsService;
    private final NewsMapper newsMapper;

    public NewsController(NewsService newsService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.newsMapper = newsMapper;
    }

    /**
     * Creates a new News entity.
     *
     * @param dto The DTO containing news data.
     * @param photo The photo file.
     * @return The created News as a response DTO.
     */
    @PostMapping("/add")
    public ResponseEntity<?> createNews(@Valid @RequestBody NewsRegisterDTO dto, @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo != null && !photo.isEmpty()) {
                dto.setPhoto(photo.getBytes());
            }
            News createdNews = newsService.createNews(dto);
            NewsResponseDTO responseDTO = newsMapper.toResponseDTO(createdNews);
            LOGGER.info("createNews - News created: {}", responseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Failed to process photo",
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("createNews - photo processing failed: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("createNews - invalid news: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("createNews - unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a News entity by its ID.
     *
     * @param id The ID of the News entity.
     * @return The News as a response DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable Long id) {
        try {
            News news = newsService.getNewsById(id);
            NewsResponseDTO responseDTO = newsMapper.toResponseDTO(news);
            LOGGER.info("getNewsById - News found: {}", responseDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (NewsNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("getNewsById - news not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("getNewsById - Unexpected Error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves all News entities.
     *
     * @return A list of News as response DTOs.
     */
    @GetMapping
    public ResponseEntity<?> getAllNews() {
        try {
            List<News> newsList = newsService.getAllNews();
            List<NewsResponseDTO> responseDTOs = newsList.stream()
                    .map(newsMapper::toResponseDTO)
                    .collect(Collectors.toList());
            LOGGER.info("getAllNews - News found: {}", responseDTOs);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("getAllNews - Unexpected Error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("creatorId") Long creatorId,
            @RequestParam("eventId") Long eventId,
            @RequestParam("photo") MultipartFile photo) {

        try {
            NewsRegisterDTO dto = new NewsRegisterDTO();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCreatorId(creatorId);
            dto.setEventId(eventId);

            if (photo != null && !photo.isEmpty()) {
                dto.setPhoto(photo.getBytes());
            }

            News updatedNews = newsService.updateNews(id, dto);
            NewsResponseDTO responseDTO = newsMapper.toResponseDTO(updatedNews);
            LOGGER.info("updateNews - News updated: {}", responseDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (IOException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Failed to process photo",
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("updateNews - photo processing failed: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (NewsNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("updateNews - news not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("updateNews - request not valid: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("updateNews - unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deletes a News entity by its ID.
     *
     * @param id The ID of the News entity to delete.
     * @return A success message upon deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            LOGGER.info("deleteNews - News deleted: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NewsNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteNews - news not found: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "News",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteNews - Unexpected error: {}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
