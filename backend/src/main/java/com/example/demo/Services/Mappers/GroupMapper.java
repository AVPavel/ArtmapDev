package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Groups.GroupRegisterDTO;
import com.example.demo.DTOs.Groups.GroupResponseDTO;
import com.example.demo.DTOs.Users.UserDTOBase;
import com.example.demo.DTOs.Users.UserResponseDTO;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.DBServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupMapper {
    private final UserService userService;

    @Autowired
    public GroupMapper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Converts a GroupRegisterDTO to a new Group entity.
     *
     * @param dto The DTO containing group data.
     * @return A new Group entity populated with data from the DTO.
     */
    public Group toEntity(GroupRegisterDTO dto, Event event) {
        Group group = new Group();
        group.setName(dto.getName()); // Ensure the name is set from the DTO
        group.setEvent(event);

        // Set Members
        Set<User> members = new HashSet<>();
        if (dto.getMemberIds() != null && !dto.getMemberIds().isEmpty()) {
            members = dto.getMemberIds().stream()
                    .map(userService::getUserById)
                    .collect(Collectors.toSet());
        } else {
            // If no members are provided, add the event organizer as the default member
            User organizer = event.getCreatedBy();
            if (organizer != null) {
                members.add(organizer);
            }
        }
        group.setMembers(members);

        return group;
    }

    /**
     * Converts a Group entity to a GroupResponseDTO.
     *
     * @param group The Group entity to convert.
     * @return A GroupResponseDTO populated with data from the entity.
     */
    public GroupResponseDTO toResponseDTO(Group group) {
        GroupResponseDTO dto = new GroupResponseDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());

        // Set Event details
        if (group.getEvent() != null) {
            dto.setEventId(group.getEvent().getId());
            dto.setEventName(group.getEvent().getTitle());
        }

        // Set Members
        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            Set<UserDTOBase> memberDTOs = group.getMembers().stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toSet());
            dto.setMembers(memberDTOs);
        }

        // Set timestamps (assuming you have them; if not, remove these lines)
        // dto.setCreatedAt(group.getCreatedAt().toString());
        dto.setUpdatedAt(group.getUpdatedAt().toString());

        return dto;
    }

    /**
     * Updates an existing Group entity with values from a GroupRegisterDTO.
     *
     * @param dto   The DTO containing updated group data.
     * @param group The existing Group entity to update.
     */
    public void updateEntityFromDTO(GroupRegisterDTO dto, Group group, Event event) {
        if (dto.getName() != null) {
            group.setName(dto.getName());
        }

        group.setEvent(event);

        group.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert.
     * @return A UserDTO populated with data from the entity.
     */
    public UserDTOBase convertUserToDTO(User user) {
        UserDTOBase dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
