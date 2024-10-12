package com.example.demo.Repositories;

import com.example.demo.DBModels.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Event> searchEventsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT e FROM Event e Where " +
            "LOWER(e.title) = LOWER(CONCAT('%', :title, '%'))")
    Optional<Event> searchEventByName(@Param("title") String title);

}
