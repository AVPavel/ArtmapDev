package com.example.demo.Controllers;

import com.example.demo.DBModels.Message;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Services.DBServices.MessageService;
import com.example.demo.Services.DBServices.UserService;
import com.example.demo.Services.Mappers.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingControllerTest {

    @Mock private MessageService messageService;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private MessageMapper messageMapper;
    @Mock private UserService userService;
    @Mock private Principal principal;

    @InjectMocks private MessagingController messagingController;

    private MessageDTO testMessageDTO;
    private User testUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");

        testMessageDTO = new MessageDTO();
        testMessageDTO.setContent("Hello");
        testMessageDTO.setEventId(1L);

        testMessage = new Message();
        testMessage.setContent("Hello");
    }

    @Test
    void sendMessage_ShouldSendToTopic() throws Exception {
        // Arrange
        when(principal.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(messageService.saveMessage(any(), any(), any())).thenReturn(testMessage);
        when(messageMapper.toDTO(any())).thenReturn(testMessageDTO);

        // Act
        messagingController.sendMessage(testMessageDTO, principal);

        // Assert
        verify(messagingTemplate).convertAndSend(
                eq("/topic/events/1"),
                eq(testMessageDTO)
        );
    }

    @Test
    void sendMessage_ShouldHandleUserNotFound() throws Exception {
        // Arrange
        when(principal.getName()).thenReturn("invaliduser");
        when(userService.getUserByUsername("invaliduser"))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act
        messagingController.sendMessage(testMessageDTO, principal);

        // Assert
        verifyNoInteractions(messageService);
        verifyNoInteractions(messagingTemplate);
    }

    @Test
    void sendMessage_ShouldHandleInvalidMessage() throws Exception {
        // Arrange
        testMessageDTO.setContent(null);
        when(principal.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(messageService.saveMessage(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid message"));

        // Act
        messagingController.sendMessage(testMessageDTO, principal);

        // Assert
        verifyNoInteractions(messagingTemplate);
    }
}