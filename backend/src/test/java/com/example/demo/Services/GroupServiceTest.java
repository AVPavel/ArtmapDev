package com.example.demo.Services;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Groups.GroupRegisterDTO;
import com.example.demo.Exceptions.Models.DuplicateResourceException;
import com.example.demo.Exceptions.Models.GroupNotFoundException;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Repositories.GroupRepository;
import com.example.demo.Services.DBServices.GroupService;
import com.example.demo.Services.DBServices.UserService;
import com.example.demo.Services.Mappers.GroupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private GroupMapper groupMapper;
    @Mock private UserService userService;

    private GroupService groupService;
    private Event testEvent;
    private User testUser;

    @BeforeEach
    void setUp() {
        groupService = new GroupService(groupRepository, groupMapper, userService);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setCreatedBy(testUser);
    }

    @Test
    void createGroup_Success() {
        // Given
        GroupRegisterDTO dto = new GroupRegisterDTO("Test Group", 1L, Set.of(1L));
        Group expectedGroup = new Group();
        expectedGroup.setName("Test Group");
        expectedGroup.setEvent(testEvent);

        given(groupRepository.existsByEventId(1L)).willReturn(false);
        given(userService.getUserById(1L)).willReturn(testUser);
        given(groupMapper.toEntity(dto, testEvent)).willReturn(expectedGroup);
        given(groupRepository.save(any())).willReturn(expectedGroup);

        // When
        Group result = groupService.createGroup(dto, testEvent);

        // Then
        assertThat(result).isEqualTo(expectedGroup);
        verify(groupRepository).save(expectedGroup);
    }

    @Test
    void createGroup_DuplicateGroup() {
        // Given
        GroupRegisterDTO dto = new GroupRegisterDTO("Test Group", 1L, Set.of(1L));
        given(groupRepository.existsByEventId(1L)).willReturn(true);

        // When/Then
        assertThatThrownBy(() -> groupService.createGroup(dto, testEvent))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Event already has a group");
    }

    @Test
    void getGroupById_NotFound() {
        given(groupRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.getGroupById(1L))
                .isInstanceOf(GroupNotFoundException.class);
    }

    @Test
    void updateGroup_ChangeEventAssociation() {
        // Given
        Group existingGroup = new Group();
        existingGroup.setEvent(testEvent);
        GroupRegisterDTO dto = new GroupRegisterDTO("New Name", 2L, Set.of(1L));

        given(groupRepository.findById(1L)).willReturn(Optional.of(existingGroup));

        // When/Then
        assertThatThrownBy(() -> groupService.updateGroup(1L, dto, testEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot change event association");
    }

    // Add more tests for other methods
}