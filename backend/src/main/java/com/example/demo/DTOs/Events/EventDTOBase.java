package com.example.demo.DTOs.Events;


import com.example.demo.DBModels.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventDTOBase {
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Organizer is mandatory")
    private Long organizerId;

    @NotNull(message = "Date is mandatory")
    private LocalDateTime date;
}
