package com.example.demo.Repositories;

import com.example.demo.DBModels.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findById(long id);

    @Query("SELECT e FROM Event e WHERE " +
            "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchEventsByKeyword(@Param("keyword") String keyword);

    Optional<Event> searchEventsByName(String name);

    Optional<Event> searchEventsByDescription(String description);

    Optional<Event> searchEventsByLocation(String location);

    Optional<Event> searchEventsByDate(LocalDateTime date);
}