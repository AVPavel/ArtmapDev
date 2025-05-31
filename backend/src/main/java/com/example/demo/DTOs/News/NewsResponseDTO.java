package com.example.demo.DTOs.News;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for responding with News data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsResponseDTO {
    private Long id;
    private String title;
    private String content;
    private Long creatorId;
    private String creatorUsername;
    private Long eventId;
    private byte[] photo;
    private String eventTitle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
