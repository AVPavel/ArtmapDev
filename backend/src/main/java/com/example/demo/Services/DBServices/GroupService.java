package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Groups.*;
import com.example.demo.Exceptions.Models.*;
import com.example.demo.Services.Mappers.GroupMapper;
import com.example.demo.Repositories.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing Group entities.
 */
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserService userService;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper, UserService userService) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
        this.userService = userService;
    }

    /**
     * Creates a new Group entity based on the provided DTO.
     *
     * @param dto The DTO containing group data.
     * @return The created Group entity.
     */
    @Transactional
    public Group createGroup(GroupRegisterDTO dto,Event event) {
        // Prevent duplicate groups for the same event
        if (groupRepository.existsByEventId(event.getId())) {
            throw new DuplicateResourceException("Event already has a group");
        }
        // Validate all member IDs exist
        Set<User> members = validateAndGetMembers(dto.getMemberIds(), event);

        Group group = groupMapper.toEntity(dto, event);
        group.setMembers(members);
        return groupRepository.save(group);
    }

    /**
     * Retrieves a Group entity by its ID.
     *
     * @param id The ID of the Group entity.
     * @return The Group entity.
     * @throws GroupNotFoundException If the Group entity is not found.
     */
    @Transactional(readOnly = true)
    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Could not find group with id: " + id));
    }

    /**
     * Retrieves a Group entity by its associated event ID.
     *
     * @param id The ID of the Event entity.
     * @return The Group entity.
     * @throws GroupNotFoundException If the Group entity is not found.
     */
    @Transactional(readOnly = true)
    public Group getGroupByEventId(Long id) {
        return groupRepository.findByEventId(id)
                .orElseThrow(() -> new GroupNotFoundException("Could not find group with event id: " + id));
    }

    /**
     * Retrieves all Group entities.
     *
     * @return A list of all Group entities.
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Updates an existing Group entity with data from the provided DTO.
     *
     * @param id  The ID of the Group entity to update.
     * @param dto The DTO containing updated group data.
     * @return The updated Group entity.
     * @throws GroupNotFoundException If the Group entity is not found.
     */
    @Transactional
    public Group updateGroup(Long id, GroupRegisterDTO dto, Event event) {
        Group existingGroup = getGroupById(id);
        // Prevent event ID modification
        if (!existingGroup.getEvent().getId().equals(dto.getEventId())) {
            throw new IllegalArgumentException("Cannot change event association");
        }

        Set<User> members = validateAndGetMembers(dto.getMemberIds(), event);
        existingGroup.setMembers(members);
        groupMapper.updateEntityFromDTO(dto, existingGroup, event);
        return groupRepository.save(existingGroup);
    }

    /**
     * Deletes a Group entity by its ID.
     *
     * @param id The ID of the Group entity to delete.
     * @throws GroupNotFoundException If the Group entity is not found.
     */
    @Transactional
    public void deleteGroup(Long id) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Could not find group with id: " + id));
        groupRepository.delete(existingGroup);
    }

    public Set<User> validateAndGetMembers(Set<Long> memberIds, Event event) {
        Set<User> members = new HashSet<>();
        if (memberIds != null && !memberIds.isEmpty()) {
            members = memberIds.stream()
                    .map(userService::getUserById)
                    .collect(Collectors.toSet());
        } else {
            // Default to event organizer
            members.add(event.getCreatedBy());
        }
        return members;
    }

}
