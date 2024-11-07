package com.example.demo.Repositories;

import com.example.demo.DBModels.Category;
import com.example.demo.DBModels.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findById(long id);

    @Query("SELECT e FROM Event e WHERE " +
            "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR e.category = :category)")
    Page<Event> searchEventsByKeywordAndCategory(@Param("keyword") String keyword,
                                                 @Param("category") Category category,
                                                 Pageable pageable);

    @Query("SELECT e FROM Event e Where " +
            "LOWER(e.title) = LOWER(CONCAT('%', :title, '%'))")
    Optional<Event> searchEventByTitle(@Param("title") String title);

    Page<Event> findAllByCategory(Category category, Pageable pageable);

}
