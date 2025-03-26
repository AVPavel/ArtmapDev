package com.example.demo.DBModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)  // Direct event relationship
    private Event event;

    @Column(nullable = false)
    private String content;

    private LocalDateTime sentAt;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", eventId=" + (event != null ? event.getId() : null) + // Only use IDs
                ", senderId=" + (sender != null ? sender.getId() : null) +
                ", sentAt=" + sentAt +
                '}';
    }
}
