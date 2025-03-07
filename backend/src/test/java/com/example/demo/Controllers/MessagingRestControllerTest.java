package com.example.demo.Controllers;

import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Services.DBServices.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingRestControllerTest {

    @Mock private MessageService messageService;
    @InjectMocks private MessagingRestController messagingRestController;

    @Test
    void getMessages_ShouldReturnMessages() {
        // Arrange
        MessageDTO messageDTO = new MessageDTO();
        List<MessageDTO> expected = List.of(messageDTO);
        when(messageService.getMessagesForEvent(1L)).thenReturn(expected);

        // Act
        ResponseEntity<List<MessageDTO>> response = messagingRestController.getMessages(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getMessages_ShouldReturnEmptyList() {
        // Arrange
        when(messageService.getMessagesForEvent(1L)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<MessageDTO>> response = messagingRestController.getMessages(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getMessages_ShouldHandleServiceErrors() {
        // Arrange
        when(messageService.getMessagesForEvent(1L))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messagingRestController.getMessages(1L);
        });
    }
}