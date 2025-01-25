package com.example.demo.DTOs.Groups;

import com.example.demo.DTOs.Users.UserDTOBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for responding with Group data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String name;
    private Long eventId;
    private String eventName;
    private Set<UserDTOBase> members;
    private String createdAt;
    private String updatedAt;
}
