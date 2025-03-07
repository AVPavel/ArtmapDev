package com.example.demo.Controllers;

import com.example.demo.DBModels.News;
import com.example.demo.DTOs.News.NewsRegisterDTO;
import com.example.demo.DTOs.News.NewsResponseDTO;
import com.example.demo.Exceptions.Models.NewsNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.NewsService;
import com.example.demo.Services.Mappers.NewsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsControllerTest {

    @Mock
    private NewsService newsService;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsController newsController;

    private NewsRegisterDTO validRegisterDTO;
    private NewsResponseDTO responseDTO;
    private News newsEntity;

    // Updated Test Setup
    @BeforeEach
    void setUp() {
        // Create a valid NewsRegisterDTO
        validRegisterDTO = new NewsRegisterDTO(
                "Breaking News",                // title
                "News content",                 // content
                1L,                            // creatorId
                2L                             // eventId (optional)
        );

        // Create a NewsResponseDTO
        responseDTO = new NewsResponseDTO(
                1L,                            // id
                "Breaking News",               // title
                "News content",                // content
                1L,                           // creatorId
                "admin",                       // creatorUsername
                2L,                           // eventId
                "Annual Conference",           // eventTitle
                LocalDateTime.now(),           // createdAt
                LocalDateTime.now()            // updatedAt
        );

        // Create a News entity
        newsEntity = new News();
        newsEntity.setId(1L);
        newsEntity.setTitle("Breaking News");
        newsEntity.setContent("News content");
        // Set other fields as needed
    }

    // Create News Tests
    @Test
    void createNews_ValidRequest_ReturnsCreated() throws Exception {
        when(newsService.createNews(any())).thenReturn(newsEntity);
        when(newsMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = newsController.createNews(validRegisterDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(newsService).createNews(validRegisterDTO);
    }

    @Test
    void createNews_InvalidData_ReturnsBadRequest() {
        when(newsService.createNews(any())).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = newsController.createNews(validRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Invalid data", error.getMessage());
    }

    @Test
    void createNews_UnexpectedError_ReturnsInternalServerError() {
        when(newsService.createNews(any())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = newsController.createNews(validRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Get News by ID Tests
    @Test
    void getNewsById_ValidId_ReturnsNews() throws Exception {
        when(newsService.getNewsById(anyLong())).thenReturn(newsEntity);
        when(newsMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = newsController.getNewsById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getNewsById_NotFound_ReturnsNotFound() {
        when(newsService.getNewsById(anyLong())).thenThrow(new NewsNotFoundException("News not found"));

        ResponseEntity<?> response = newsController.getNewsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("News not found", error.getMessage());
    }

    @Test
    void getNewsById_UnexpectedError_ReturnsInternalServerError() {
        when(newsService.getNewsById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = newsController.getNewsById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Get All News Tests
    @Test
    void getAllNews_Success_ReturnsNewsList() {
        List<News> newsList = List.of(newsEntity);
        when(newsService.getAllNews()).thenReturn(newsList);
        when(newsMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = newsController.getAllNews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> responseList = (List<?>) response.getBody();
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals(responseDTO, responseList.get(0));
    }

    @Test
    void getAllNews_EmptyList_ReturnsEmptyList() {
        when(newsService.getAllNews()).thenReturn(List.of());

        ResponseEntity<?> response = newsController.getAllNews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> responseList = (List<?>) response.getBody();
        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }

    @Test
    void getAllNews_UnexpectedError_ReturnsInternalServerError() {
        when(newsService.getAllNews()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = newsController.getAllNews();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Update News Tests
    @Test
    void updateNews_ValidRequest_ReturnsUpdatedNews() throws Exception {
        when(newsService.updateNews(anyLong(), any())).thenReturn(newsEntity);
        when(newsMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = newsController.updateNews(1L, validRegisterDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(newsService).updateNews(1L, validRegisterDTO);
    }

    @Test
    void updateNews_NotFound_ReturnsNotFound() {
        when(newsService.updateNews(anyLong(), any()))
                .thenThrow(new NewsNotFoundException("News not found"));

        ResponseEntity<?> response = newsController.updateNews(1L, validRegisterDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("News not found", error.getMessage());
    }

    @Test
    void updateNews_InvalidData_ReturnsBadRequest() {
        when(newsService.updateNews(anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = newsController.updateNews(1L, validRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Invalid data", error.getMessage());
    }

    @Test
    void updateNews_UnexpectedError_ReturnsInternalServerError() {
        when(newsService.updateNews(anyLong(), any()))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = newsController.updateNews(1L, validRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Delete News Tests
    @Test
    void deleteNews_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(newsService).deleteNews(anyLong());

        ResponseEntity<?> response = newsController.deleteNews(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(newsService).deleteNews(1L);
    }

    @Test
    void deleteNews_NotFound_ReturnsNotFound() {
        doThrow(new NewsNotFoundException("News not found"))
                .when(newsService).deleteNews(anyLong());

        ResponseEntity<?> response = newsController.deleteNews(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("News not found", error.getMessage());
    }

    @Test
    void deleteNews_UnexpectedError_ReturnsInternalServerError() {
        doThrow(new RuntimeException("Database error"))
                .when(newsService).deleteNews(anyLong());

        ResponseEntity<?> response = newsController.deleteNews(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Error Response Validation
    @Test
    void errorResponse_ContainsCorrectFields() {
        ErrorResponse error = new ErrorResponse(
                404,
                "Not found",
                "News",
                LocalDateTime.now()
        );

        assertEquals(404, error.getStatus());
        assertEquals("Not found", error.getMessage());
        assertEquals("News", error.getEntity());
        assertNotNull(error.getTimestamp());
    }
}