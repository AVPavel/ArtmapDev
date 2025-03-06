package com.example.demo.Mappers;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Groups.GroupRegisterDTO;
import com.example.demo.DTOs.Groups.GroupResponseDTO;
import com.example.demo.DTOs.Users.UserDTOBase;
import com.example.demo.Services.DBServices.UserService;
import com.example.demo.Services.Mappers.GroupMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GroupMapperTest {

    @Mock private UserService userService;
    @InjectMocks private GroupMapper groupMapper;

    private final User testUser = new User(1L, "user", "user@test.com");
    private final Event testEvent = new Event(1L, "Test Event", null, null, null, null, null, null, null, null, null);

    @Test
    void toEntity_WithMembers() {
        // Given
        GroupRegisterDTO dto = new GroupRegisterDTO("Test Group", 1L, Set.of(1L));
        given(userService.getUserById(1L)).willReturn(testUser);

        // When
        Group result = groupMapper.toEntity(dto, testEvent);

        // Then
        assertThat(result.getName()).isEqualTo("Test Group");
        assertThat(result.getMembers()).hasSize(1);
    }

    @Test
    void toResponseDTO_FullMapping() {
        // Given
        Group group = new Group();
        group.setId(1L);
        group.setName("Test Group");
        group.setEvent(testEvent);
        group.setMembers(Set.of(testUser));
        group.setUpdatedAt(LocalDateTime.now());

        // When
        GroupResponseDTO dto = groupMapper.toResponseDTO(group);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEventName()).isEqualTo("Test Event");
        assertThat(dto.getMembers()).hasSize(1);
    }

    @Test
    void updateEntityFromDTO_NameUpdate() {
        // Given
        Group group = new Group();
        group.setName("Old Name");
        GroupRegisterDTO dto = new GroupRegisterDTO("New Name", 1L, Set.of(1L));

        // When
        groupMapper.updateEntityFromDTO(dto, group, testEvent);

        // Then
        assertThat(group.getName()).isEqualTo("New Name");
    }
}