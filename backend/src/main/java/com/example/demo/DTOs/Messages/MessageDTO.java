package com.example.demo.DTOs.Messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    @NotBlank(message = "Sender is mandatory")
    private String sender;

    @NotBlank(message = "The message cannot be empty")
    private String content;

    @NotNull(message = "Event ID is mandatory")
    private Long eventId;

    private LocalDateTime timestamp;

    // Add constructor
    public MessageDTO(Long id, String content, String sender, LocalDateTime timestamp, Long eventId) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.eventId = eventId;
    }
}
