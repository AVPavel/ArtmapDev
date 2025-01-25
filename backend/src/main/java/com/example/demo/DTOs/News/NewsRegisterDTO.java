package com.example.demo.DTOs.News;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for registering (creating/updating) News.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsRegisterDTO {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    @Size(max = 5000, message = "Content can have at most 5000 characters")
    private String content;

    @NotNull(message = "Creator ID is mandatory")
    private Long creatorId;

    private Long eventId;
}
