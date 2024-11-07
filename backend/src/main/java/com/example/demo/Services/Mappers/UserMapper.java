package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Users.UserRegisterDTO;
import com.example.demo.DTOs.Users.UserResponseDTO;
import com.example.demo.DTOs.Users.UserDTOBase;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    // Convert User to UserRegisterDTO
    public UserRegisterDTO toRegistrationDTO(User user) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole().name());
        dto.setPreferredBudget(user.getPreferredBudget());
        return dto;
    }

    // Convert User to UserResponseDTO
    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setPreferredBudget(user.getPreferredBudget());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    // Generic method to convert a DTOBase (both UserDTOBase and UserRegisterDTO) to a User entity
    public User toEntity(UserDTOBase userDTO) {
        User user = new User();

        // Common field mapping logic for both UserDTOBase and UserRegisterDTO
        if (userDTO.getUsername() != null && !userDTO.getUsername().trim().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        } else {
            throw new IllegalArgumentException("Username is required");
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().trim().isEmpty()) {
            user.setEmail(userDTO.getEmail());
        } else {
            throw new IllegalArgumentException("Email is required");
        }

        if (userDTO.getId() != null) {
            user.setId(userDTO.getId());
        }

        if (userDTO.getRole() != null && !userDTO.getRole().trim().isEmpty()) {
            try {
                user.setRole(User.Role.valueOf(userDTO.getRole()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role provided");
            }
        } else {
            user.setRole(User.Role.USER); // Rol implicit
        }

        return user;
    }

    // Convert UserRegisterDTO to User, which requires password and preferredBudget handling in addition to common fields
    public User toEntity(UserRegisterDTO userDTO) {
        User user = toEntity((UserDTOBase) userDTO); // Reuse common logic

        // Specific handling for password in UserRegisterDTO
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        } else {
            throw new IllegalArgumentException("Password is required");
        }

        // Specific handling for preferredBudget in UserRegisterDTO
        if (userDTO.getPreferredBudget() != null) {
            user.setPreferredBudget(userDTO.getPreferredBudget());
        }

        return user;
    }
}
