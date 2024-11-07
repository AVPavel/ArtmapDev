package com.example.demo.DTOs.Events;

import com.example.demo.DBModels.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String address;
    private LocalDateTime date;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long organizerId;
    private String organizerUsername;
    private Long categoryId;
    private String categoryName;
    private Set<String> genreName;
    private Integer popularity;
    private String ticketPrices; // JSON string
    private BigDecimal cheapestTicket;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
