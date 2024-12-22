package com.example.demo.Controllers;

import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Services.DBServices.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessagingRestController {
    @Autowired
    private final MessageService messageService;
    public MessagingRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping("/{eventId}")
    public ResponseEntity<?> getMessages(@PathVariable Long eventId) {
        List<MessageDTO> messageDTOS = messageService.getMessagesForEvent(eventId);

        return null;
    }
}
