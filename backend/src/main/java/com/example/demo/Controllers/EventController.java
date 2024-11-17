package com.example.demo.Controllers;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.DTOs.Events.EventRegisterDTO;
import com.example.demo.DTOs.Events.EventResponseDTO;
import com.example.demo.Exceptions.DuplicateResourceException;
import com.example.demo.Exceptions.EventNotFoundException;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.Mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @Autowired
    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        try {
            Event event = eventService.getEventById(id);
            EventResponseDTO responseDTO = eventMapper.toResponseDTO(event);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (EventNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<EventResponseDTO>> searchEvents(
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
            return new ResponseEntity<>(responseEventsPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<EventResponseDTO> addEvent(@RequestBody EventRegisterDTO eventRegisterDTO) {
        try {
            Event event = eventMapper.toEntity(eventRegisterDTO);
            Event savedEvent = eventService.addEvent(event);
            EventResponseDTO responseDTO = eventMapper.toResponseDTO(savedEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (DuplicateResourceException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception ex) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
