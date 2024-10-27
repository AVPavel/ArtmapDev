package com.example.demo.DBModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_event_interactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    private Integer rating; // Opțional

    @Column(length = 2000)
    private String review; // Opțional

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    private void onCreate(){
        timestamp = LocalDateTime.now();
    }
    public enum InteractionType {
        WANT_TO_ATTEND,      // „Vreau să particip”
        SAVED,               // „Salvare”
        ATTENDING,           // „Particip”
        INTERESTED,          // „Interesat”
        NOT_INTERESTED,      // „Nu mă interesează”
        LIKED,               // „Apreciere”
        DISLIKED             // „Nu mi-a plăcut”
    }
}
