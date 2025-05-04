package com.example.demo.Repositories;

import com.example.demo.DBModels.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<User> findByRole(User.Role role, Pageable pageable);


    @Query("SELECT u from User u where LOWER(u.username) like LOWER(CONCAT('%',:searchTerm,'%'))" +
            "OR LOWER(u.email) like LOWER(CONCAT('%',:searchTerm,'%')) ")
    Page<User> searchUsers(@Param("searchTerm")String searchTerm, Pageable pageable);

    @Query("SELECT u from User u join u.eventsParticipating e where e.id = :eventId")
    Page<User> findUsersByParticipatingEvent(@Param("eventId") String eventId, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.eventsParticipating WHERE u.username = :username")
    Optional<User> findByUsernameWithEventsParticipating(String username);
}
