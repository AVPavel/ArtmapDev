package com.example.demo.DTOs.UserPreferences;

import lombok.Data;

// DTO for request body
@Data
public class PreferenceRequest {
    private Long categoryId;
    private Long genreId;
}
