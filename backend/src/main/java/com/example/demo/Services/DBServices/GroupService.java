package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Group;
import com.example.demo.DTOs.Groups.*;
import com.example.demo.Exceptions.Models.*;
import com.example.demo.Globals.GlobalLogger;
import com.example.demo.Services.Mappers.GroupMapper;
import com.example.demo.Repositories.GroupRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Group entities.
 */
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public GroupService(GroupRepository groupRepository, GroupMapper groupMapper) {
        this.groupRepository = groupRepository;
        this.groupMapper = groupMapper;
    }

    /**
     * Creates a new Group entity based on the provided DTO.
     *
     * @param dto The DTO containing group data.
     * @return The created Group entity.
     */
    @Transactional
    public Group createGroup(GroupRegisterDTO dto) {
        Group group = groupMapper.toEntity(dto);
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
    public Group updateGroup(Long id, GroupRegisterDTO dto) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Could not find group with id: " + id));

        groupMapper.updateEntityFromDTO(dto, existingGroup);
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
}
