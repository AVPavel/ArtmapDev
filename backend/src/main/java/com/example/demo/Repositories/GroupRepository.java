package com.example.demo.Repositories;

import com.example.demo.DBModels.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByEventId(Long eventId);
    boolean existsByEventId(Long eventId);
}
