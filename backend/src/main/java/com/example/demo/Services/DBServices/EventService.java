package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import com.example.demo.Exceptions.DuplicateResourceException;
import com.example.demo.Exceptions.EventNotFoundException;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.Mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(()
                -> new EventNotFoundException("Could not find event with id: " + id));
    }

    @Transactional
    public Event addEvent(Event event) {
        if (eventRepository.searchEventByTitle(event.getTitle()).isPresent()) {
            throw new DuplicateResourceException("Event with title: " + event.getTitle() + " already exists");
        }


        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Page<Event> searchEvents(String title, Category category, int page, int size, String sortBy, String order) {
        Pageable pageable = PageRequest.of(page, size,
                order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        if (title != null && !title.isEmpty()) {
            return eventRepository.searchEventsByKeywordAndCategory(title, category, pageable);
        }
        if(category != null && !category.getName().equalsIgnoreCase("all")) {
            return eventRepository.findAllByCategory(category, pageable);
        }
        return eventRepository.findAll(pageable);
    }

}
