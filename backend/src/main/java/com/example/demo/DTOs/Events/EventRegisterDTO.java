package com.example.demo.DTOs.Events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegisterDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Organizer ID is mandatory")
    private Long organizerId;

    @NotNull(message = "Date is mandatory")
    private LocalDateTime date;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 2000, message = "Description can have at most 2000 characters")
    private String description;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId; // ID-ul categoriei evenimentului
}
