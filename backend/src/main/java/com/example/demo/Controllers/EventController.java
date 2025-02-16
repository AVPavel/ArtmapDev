package com.example.demo.Controllers;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.EventNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.Mappers.EventMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Autowired
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }
    @GetMapping("{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            EventResponseDTO responseDTO = eventMapper.toResponseDTO(event);
            LOGGER.info("getEventById - event found:{}", event);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("getEventById - event not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
         catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Prices or mapWrapper",
                    LocalDateTime.now()
            );
            LOGGER.error("getEventById - Unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
         }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchEvents(
            @RequestParam() String searchTerm,
            @RequestParam(required = false, defaultValue = "") Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        try {
            Page<Event> events = eventService.searchEvents(searchTerm, category, page, size, sortBy, sortDirection);
            Page<EventResponseDTO> responseEventsPage = events.map(eventMapper::toResponseDTO);
            LOGGER.info("searchEvents - events found:{}", events);
            return new ResponseEntity<>(responseEventsPage, HttpStatus.OK);
        }
        catch (IllegalArgumentException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Prices or mapWrapper",
                    LocalDateTime.now()
            );
            LOGGER.error("searchEvents - request not valid :{}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("searchEvents - event not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addEvent(@RequestBody EventRegisterDTO eventRegisterDTO, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Event event = eventMapper.toEntity(eventRegisterDTO);
            Event savedEvent = eventService.addEvent(event, username);
            EventResponseDTO responseDTO = eventMapper.toResponseDTO(savedEvent);
            LOGGER.info("addEvent - event found:{}", event);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (DuplicateResourceException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("addEvent - duplicate event:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (IllegalArgumentException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Prices of mapWrapper",
                    LocalDateTime.now()
            );
            LOGGER.error("addEvent - request not valid:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Error",
                    LocalDateTime.now()
            );
            LOGGER.error("addEvent - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRegisterDTO eventRegisterDTO) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventRegisterDTO);
            EventResponseDTO responseDTO = eventMapper.toResponseDTO(updatedEvent);
            LOGGER.info("updateEvent - event found:{}", updatedEvent);
            return ResponseEntity.ok(responseDTO);
        } catch (EventNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("updateEvent - duplicate event:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("updateEvent - request not valid:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Event",
                    LocalDateTime.now()
            );
            LOGGER.error("updateEvent - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}