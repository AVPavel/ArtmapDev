package com.example.demo.Repositories;

import com.example.demo.DBModels.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Simplified query with direct event relationship
    @Query("SELECT m FROM Message m WHERE m.event.id = :eventId ORDER BY m.sentAt ASC")
    List<Message> findAllByEventId(@Param("eventId") Long eventId);
}
