package com.example.demo.DBModels;

import com.example.demo.Converters.TicketPricesConverter;
import com.example.demo.Models.TicketPrices;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 6)
    private BigDecimal longitude;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Convert(converter = TicketPricesConverter.class)
    @Column(columnDefinition = "JSON")
    private TicketPrices ticketPrices;

    @Column(name = "cheapest_ticket", precision = 10, scale = 2)
    private BigDecimal cheapestTicket;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Event-Genre relationship
    @ManyToMany
    @ToString.Exclude
    @JoinTable(
            name = "event_genres",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    public Event(long l, String testEvent, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        setId(l);
        setTitle(testEvent);
        setDescription(o.toString());
    }

    @PrePersist
    private void onCreated() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdated() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserEventInteraction> interactions;
}
