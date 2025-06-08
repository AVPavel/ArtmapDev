package com.example.demo.Controllers;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.EventNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.CategoryService;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.Mappers.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private EventMapper eventMapper;
    @Mock
    private CategoryService categoryService;  // Add this mock

    @InjectMocks
    private EventController eventController;

    private Event testEvent;
    private EventResponseDTO testResponseDTO;
    private EventRegisterDTO testRegisterDTO;
    private Category testCategory;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");

        testCategory = new Category(1L, "CONCERT");
        testCategory.setId(1L);
        testCategory.setName("CONCERT");

        testResponseDTO = new EventResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setTitle("Test Event");

        testRegisterDTO = new EventRegisterDTO();
        testRegisterDTO.setTitle("Test Event");

        UserDetails userDetails = User.withUsername("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
        authentication = new TestingAuthenticationToken(userDetails, null);
    }

    // getEventById Tests
    @Test
    void getEventById_WhenEventExists_ReturnsEvent() {
        when(eventService.getEventById(1L)).thenReturn(testEvent);
        when(eventMapper.toResponseDTO(testEvent)).thenReturn(testResponseDTO);

        ResponseEntity<?> response = eventController.getEventById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponseDTO, response.getBody());
        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_WhenEventNotFound_ReturnsNotFound() {
        when(eventService.getEventById(1L)).thenThrow(new EventNotFoundException("Event not found"));

        ResponseEntity<?> response = eventController.getEventById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_WhenIllegalArgument_ReturnsInternalServerError() {
        when(eventService.getEventById(1L)).thenThrow(new IllegalArgumentException("Invalid argument"));

        ResponseEntity<?> response = eventController.getEventById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        verify(eventService).getEventById(1L);
    }

    // searchEvents Tests
    @Test
    void searchEvents_WithValidParameters_ReturnsPageOfEvents() {
        Category testCategory = new Category(1L, "CONCERT");
        testCategory.setDescription("Music concerts");
        testCategory.setHasGenre(true);

        Page<Event> page = new PageImpl<>(List.of(testEvent));

        when(eventService.searchEvents("test", testCategory, 0, 50, "date", "asc"))
                .thenReturn(page);

        when(eventMapper.toResponseDTO(testEvent)).thenReturn(testResponseDTO);

        ResponseEntity<?> response = eventController.searchEvents(
                "test", testCategory, 0, 50, "date", "asc"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertTrue(response.getBody() instanceof Page, "Response body should be a Page");

        verify(eventService).searchEvents("test", testCategory, 0, 50, "date", "asc");
        verify(eventMapper).toResponseDTO(testEvent);

        verifyNoMoreInteractions(eventService, eventMapper);
    }

    @Test
    void searchEvents_WithInvalidSortBy_ReturnsBadRequest() {
        when(eventService.searchEvents(any(), any(), anyInt(), anyInt(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid sort parameter"));

        ResponseEntity<?> response = eventController.searchEvents(
                "test", null, 0, 50, "invalid", "asc"
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void searchEvents_WhenNoEventsFound_ReturnsNotFound() {
        when(eventService.searchEvents(any(), any(), anyInt(), anyInt(), any(), any()))
                .thenThrow(new EventNotFoundException("No events found"));

        ResponseEntity<?> response = eventController.searchEvents(
                "test", null, 0, 50, "date", "asc"
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
    }

    // addEvent Tests
    @Test
    void addEvent_WithValidInput_ReturnsCreatedEvent() {
        when(eventMapper.toEntity(testRegisterDTO)).thenReturn(testEvent);
        when(eventService.addEvent(testEvent, "testuser")).thenReturn(testEvent);
        when(eventMapper.toResponseDTO(testEvent)).thenReturn(testResponseDTO);

        ResponseEntity<?> response = eventController.addEvent(testRegisterDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testResponseDTO, response.getBody());
        verify(eventService).addEvent(testEvent, "testuser");
    }

    @Test
    void addEvent_WithDuplicateEvent_ReturnsConflict() {
        when(eventMapper.toEntity(testRegisterDTO)).thenReturn(testEvent);
        when(eventService.addEvent(testEvent, "testuser"))
                .thenThrow(new DuplicateResourceException("Event exists"));

        ResponseEntity<?> response = eventController.addEvent(testRegisterDTO);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void addEvent_WithInvalidInput_ReturnsBadRequest() {
        when(eventMapper.toEntity(testRegisterDTO)).thenReturn(testEvent);
        when(eventService.addEvent(testEvent, "testuser"))
                .thenThrow(new IllegalArgumentException("Invalid prices"));

        ResponseEntity<?> response = eventController.addEvent(testRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());
    }

    @Test
    void addEvent_WithUnexpectedError_ReturnsInternalServerError() {
        when(eventMapper.toEntity(testRegisterDTO)).thenReturn(testEvent);
        when(eventService.addEvent(testEvent, "testuser"))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = eventController.addEvent(testRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    // updateEvent Tests
    @Test
    void updateEvent_WithValidInput_ReturnsUpdatedEvent() {
        when(eventService.updateEvent(1L, testRegisterDTO)).thenReturn(testEvent);
        when(eventMapper.toResponseDTO(testEvent)).thenReturn(testResponseDTO);

        ResponseEntity<?> response = eventController.updateEvent(1L, testRegisterDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testResponseDTO, response.getBody());
        verify(eventService).updateEvent(1L, testRegisterDTO);
    }

    @Test
    void updateEvent_WhenEventNotFound_ReturnsNotFound() {
        when(eventService.updateEvent(1L, testRegisterDTO))
                .thenThrow(new EventNotFoundException("Event not found"));

        ResponseEntity<?> response = eventController.updateEvent(1L, testRegisterDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void updateEvent_WithInvalidInput_ReturnsBadRequest() {
        when(eventService.updateEvent(1L, testRegisterDTO))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = eventController.updateEvent(1L, testRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }

    @Test
    void updateEvent_WithUnexpectedError_ReturnsInternalServerError() {
        when(eventService.updateEvent(1L, testRegisterDTO))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = eventController.updateEvent(1L, testRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
    }
}