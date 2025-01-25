package com.example.demo.DTOs.Groups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for registering (creating/updating) Group.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRegisterDTO {

    @NotBlank(message = "Group name is mandatory")
    @Size(max = 255, message = "Group name can have at most 255 characters")
    private String name;

    @NotNull(message = "Event ID is mandatory")
    private Long eventId;

    private Set<Long> memberIds;
}