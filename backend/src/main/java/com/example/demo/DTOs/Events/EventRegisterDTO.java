package com.example.demo.DTOs.Events;

import com.example.demo.DBModels.Event;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegisterDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 2000, message = "Description can have at most 2000 characters")
    private String description;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotNull(message = "Date is mandatory")
    private LocalDateTime date;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "Organizer ID is mandatory")
    private Long organizerId;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    @NotBlank(message = "Genre is mandatory")
    private Set<Long> genreId;

    private String ticketPrices; // JSON string

    private BigDecimal cheapestTicket;
}
