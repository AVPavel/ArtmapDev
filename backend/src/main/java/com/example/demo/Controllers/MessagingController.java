package com.example.demo.Controllers;

import com.example.demo.DBModels.Message;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Globals.GlobalLogger;
import com.example.demo.Services.DBServices.MessageService;
import com.example.demo.Services.DBServices.UserService;
import com.example.demo.Services.Mappers.MessageMapper;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessagingController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messageTemplate;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private static final Logger logger = GlobalLogger.getLogger(MessagingController.class);

    public MessagingController(
            MessageService messageService,
            SimpMessagingTemplate messageTemplate,
            MessageMapper messageMapper,
            UserService userService)
    {
        this.messageService = messageService;
        this.messageTemplate = messageTemplate;
        this.messageMapper = messageMapper;
        this.userService = userService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageDTO messageDTO, Principal principal) {
        logger.debug("Received message: {}", messageDTO);
        try {
            User sender = userService.getUserByUsername(principal.getName());

            Message savedMessage = messageService.saveMessage(
                    messageDTO.getContent(),
                    messageDTO.getEventId(),
                    sender
            );

            messageTemplate.convertAndSend(
                    "/topic/events/" + messageDTO.getEventId(),
                    messageMapper.toDTO(savedMessage)
            );
        }
        catch (UserNotFoundException | IllegalArgumentException e) {
            logger.error("Message sending failed: {}", e.getMessage());
        }
    }

}
