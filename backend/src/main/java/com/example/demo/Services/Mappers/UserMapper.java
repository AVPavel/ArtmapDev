package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.User;
import com.example.demo.DTOs.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
    public User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setId(userDTO.getId());
        user.setRole(User.Role.valueOf(userDTO.getRole()));
        return user;
    }
}
