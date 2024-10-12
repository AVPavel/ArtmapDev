package com.example.demo.DTOs.Events;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO extends EventDTOBase {
    private String description;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Poți adăuga detalii despre categorie sau organizator dacă este necesar
    private String categoryName;
    private String organizerUsername;


}
