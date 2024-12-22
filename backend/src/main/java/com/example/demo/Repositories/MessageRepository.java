package com.example.demo.Repositories;

import com.example.demo.DBModels.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "JOIN m.group g " +
            "JOIN g.event e " +
            "WHERE e.id = :eventId")
    List<Message> findAllMessagesByEventId(@Param("eventId") Long eventId);
}
