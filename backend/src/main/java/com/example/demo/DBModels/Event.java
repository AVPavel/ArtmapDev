package com.example.demo.DBModels;

import com.example.demo.Converters.TicketPricesConverter;
import com.example.demo.Models.TicketPrices;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
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
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 50)
    private String genre;

    @Column(nullable = false)
    private Integer popularity;

    @Column(columnDefinition = "JSON")
    @Convert(converter = TicketPricesConverter.class)
    private TicketPrices ticketPrices;

    @Column(name = "cheapest_ticket", precision = 10, scale = 2)
    private BigDecimal cheapestTicket;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreated(){
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    private void onUpdated(){
        updatedAt = LocalDateTime.now();
    }

    // Relația Many-to-Many cu User pentru interacțiuni
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserEventInteraction> interactions;
}
