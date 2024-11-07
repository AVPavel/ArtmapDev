package com.example.demo.DTOs.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO extends UserDTOBase {
    private BigDecimal preferredBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
