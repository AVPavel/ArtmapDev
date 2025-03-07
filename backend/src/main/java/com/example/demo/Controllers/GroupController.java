package com.example.demo.Controllers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DTOs.Groups.*;
import com.example.demo.Exceptions.Models.EventNotFoundException;
import com.example.demo.Exceptions.Models.GroupNotFoundException;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.Mappers.GroupMapper;
import com.example.demo.Models.*;
import com.example.demo.Services.DBServices.GroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing Group entities.
 */
@RestController
@RequestMapping("api/groups")
public class GroupController {
    private final static Logger LOGGER = LoggerFactory.getLogger(GroupController.class);
    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final EventService eventService;

    public GroupController(GroupService groupService, GroupMapper groupMapper, EventService eventService) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.eventService = eventService;
    }

    /**
     * Creates a new Group entity.
     *
     * @param dto The DTO containing group data.
     * @return The created Group as a response DTO.
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupRegisterDTO dto) {
        try {
            Event event = eventService.getEventById(dto.getEventId());
            Group createdGroup = groupService.createGroup(dto, event);
            GroupResponseDTO responseDTO = groupMapper.toResponseDTO(createdGroup);
            LOGGER.info("createGroup - group created: {}", responseDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("createGroup - request not valid:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        catch (EventNotFoundException e){
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage() + "(Tried to create a group)",
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("createGroup - group not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("createGroup - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a Group entity by its ID.
     *
     * @param id The ID of the Group entity.
     * @return The Group as a response DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        try {
            Group group = groupService.getGroupById(id);
            GroupResponseDTO responseDTO = groupMapper.toResponseDTO(group);
            LOGGER.info("getGroupById - group found: {}", responseDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (GroupNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("getGroupById - group not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("getGroupById - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves all Group entities.
     *
     * @return A list of Groups as response DTOs.
     */
    @Transactional
    @GetMapping("/all")
    public ResponseEntity<?> getAllGroups() {
        try {
            List<Group> groups = groupService.getAllGroups();
            List<GroupResponseDTO> responseDTOs = groups.stream()
                    .map(groupMapper::toResponseDTO)
                    .collect(Collectors.toList());
            LOGGER.info("getAllGroups - groups found: {}", responseDTOs);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("getAllGroups - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    /**
     * Updates an existing Group entity.
     *
     * @param id  The ID of the Group entity to update.
     * @param dto The DTO containing updated group data.
     * @return The updated Group as a response DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupRegisterDTO dto) {
        try {
            Event event = eventService.getEventById(dto.getEventId());
            Group updatedGroup = groupService.updateGroup(id, dto,event);
            GroupResponseDTO responseDTO = groupMapper.toResponseDTO(updatedGroup);
            LOGGER.info("updateGroup - group updated: {}", responseDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (GroupNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("updateGroup - group not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("updateGroup - request not valid:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("updateGroup - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deletes a Group entity by its ID.
     *
     * @param id The ID of the Group entity to delete.
     * @return A success message upon deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        try {
            groupService.deleteGroup(id);
            LOGGER.info("deleteGroup - group deleted: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (GroupNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteGroup - group not found:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    "Group",
                    LocalDateTime.now()
            );
            LOGGER.error("deleteGroup - unexpected error:{}", errorResponse);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
