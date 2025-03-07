package com.example.demo.Controllers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DTOs.Groups.GroupRegisterDTO;
import com.example.demo.DTOs.Groups.GroupResponseDTO;
import com.example.demo.Exceptions.Models.EventNotFoundException;
import com.example.demo.Exceptions.Models.GroupNotFoundException;
import com.example.demo.Models.ErrorResponse;
import com.example.demo.Services.DBServices.EventService;
import com.example.demo.Services.DBServices.GroupService;
import com.example.demo.Services.Mappers.GroupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private EventService eventService;

    @InjectMocks
    private GroupController groupController;

    private GroupRegisterDTO validRegisterDTO;
    private GroupResponseDTO responseDTO;
    private Group group;
    private Event event;

    @BeforeEach
    void setUp() {
        validRegisterDTO = new GroupRegisterDTO(
                "Group Name",  // name
                1L,            // eventId
                Set.of(10L)    // memberIds (assuming 10 is a user ID)
        );

        responseDTO = new GroupResponseDTO(
                1L,                      // id
                "Group Name",            // name
                1L,                      // eventId
                "Event Name",            // eventName
                Collections.emptySet(),  // members (Set<UserDTOBase>)
                "2023-01-01T12:00:00",  // createdAt
                "2023-01-02T12:00:00"   // updatedAt
        );

        group = new Group();
        group.setId(1L);

        event = new Event();
        event.setId(1L);
    }

    // Create Group Tests
    @Test
    void createGroup_ValidRequest_ReturnsCreated() throws Exception {
        when(eventService.getEventById(anyLong())).thenReturn(event);
        when(groupService.createGroup(any(), any())).thenReturn(group);
        when(groupMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = groupController.createGroup(validRegisterDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(groupService).createGroup(validRegisterDTO, event);
    }

    @Test
    void createGroup_EventNotFound_ReturnsNotFound() throws Exception {
        when(eventService.getEventById(anyLong())).thenThrow(new EventNotFoundException("Event not found"));

        ResponseEntity<?> response = groupController.createGroup(validRegisterDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertTrue(error.getMessage().contains("(Tried to create a group)"));
    }

    @Test
    void createGroup_InvalidData_ReturnsBadRequest() throws Exception {
        when(eventService.getEventById(anyLong())).thenReturn(event);
        when(groupService.createGroup(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid group data"));

        ResponseEntity<?> response = groupController.createGroup(validRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Invalid group data", error.getMessage());
    }

    @Test
    void createGroup_UnexpectedError_ReturnsInternalServerError() throws Exception {
        when(eventService.getEventById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = groupController.createGroup(validRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Get Group by ID Tests
    @Test
    void getGroupById_ValidId_ReturnsGroup() throws Exception {
        when(groupService.getGroupById(anyLong())).thenReturn(group);
        when(groupMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = groupController.getGroupById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void getGroupById_GroupNotFound_ReturnsNotFound() throws Exception {
        when(groupService.getGroupById(anyLong())).thenThrow(new GroupNotFoundException("Group not found"));

        ResponseEntity<?> response = groupController.getGroupById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Group not found", error.getMessage());
    }

    @Test
    void getGroupById_UnexpectedError_ReturnsInternalServerError() throws Exception {
        when(groupService.getGroupById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = groupController.getGroupById(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Get All Groups Tests
    @Test
    void getAllGroups_Success_ReturnsGroupsList() throws Exception {
        List<Group> groups = Collections.singletonList(group);
        when(groupService.getAllGroups()).thenReturn(groups);
        when(groupMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = groupController.getAllGroups();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> responseList = (List<?>) response.getBody();
        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        assertEquals(responseDTO, responseList.getFirst());
    }

    @Test
    void getAllGroups_EmptyList_ReturnsEmptyList() throws Exception {
        when(groupService.getAllGroups()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = groupController.getAllGroups();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> responseList = (List<?>) response.getBody();
        assertNotNull(responseList);
        assertTrue(responseList.isEmpty());
    }

    @Test
    void getAllGroups_UnexpectedError_ReturnsInternalServerError() throws Exception {
        when(groupService.getAllGroups()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = groupController.getAllGroups();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Update Group Tests
    @Test
    void updateGroup_ValidRequest_ReturnsUpdatedGroup() throws Exception {
        when(eventService.getEventById(anyLong())).thenReturn(event);
        when(groupService.updateGroup(anyLong(), any(), any())).thenReturn(group);
        when(groupMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ResponseEntity<?> response = groupController.updateGroup(1L, validRegisterDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(groupService).updateGroup(1L, validRegisterDTO, event);
    }

    @Test
    void updateGroup_GroupNotFound_ReturnsNotFound() throws Exception {
        when(eventService.getEventById(anyLong())).thenReturn(event);
        when(groupService.updateGroup(anyLong(), any(), any()))
                .thenThrow(new GroupNotFoundException("Group not found"));

        ResponseEntity<?> response = groupController.updateGroup(1L, validRegisterDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Group not found", error.getMessage());
    }

    @Test
    void updateGroup_InvalidData_ReturnsBadRequest() throws Exception {
        when(eventService.getEventById(anyLong())).thenReturn(event);
        when(groupService.updateGroup(anyLong(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid update data"));

        ResponseEntity<?> response = groupController.updateGroup(1L, validRegisterDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Invalid update data", error.getMessage());
    }

    @Test
    void updateGroup_UnexpectedError_ReturnsInternalServerError() throws Exception {
        when(eventService.getEventById(anyLong())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = groupController.updateGroup(1L, validRegisterDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Delete Group Tests
    @Test
    void deleteGroup_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(groupService).deleteGroup(anyLong());

        ResponseEntity<?> response = groupController.deleteGroup(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(groupService).deleteGroup(1L);
    }

    @Test
    void deleteGroup_GroupNotFound_ReturnsNotFound() throws Exception {
        doThrow(new GroupNotFoundException("Group not found"))
                .when(groupService).deleteGroup(anyLong());

        ResponseEntity<?> response = groupController.deleteGroup(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Group not found", error.getMessage());
    }

    @Test
    void deleteGroup_UnexpectedError_ReturnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(groupService).deleteGroup(anyLong());

        ResponseEntity<?> response = groupController.deleteGroup(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertNotNull(error);
        assertEquals("Database error", error.getMessage());
    }

    // Error Response Validation
    @Test
    void errorResponse_ContainsCorrectFields() {
        ErrorResponse error = new ErrorResponse(
                404,
                "Group not found",
                "Group",
                LocalDateTime.now()
        );

        assertEquals(404, error.getStatus());
        assertEquals("Group not found", error.getMessage());
        assertEquals("Group", error.getEntity());
        assertNotNull(error.getTimestamp());
    }
}