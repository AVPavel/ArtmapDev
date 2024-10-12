package com.example.demo.DTOs.Events;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegisterDTO extends EventDTOBase{
    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId; // ID-ul categoriei evenimentului
}
