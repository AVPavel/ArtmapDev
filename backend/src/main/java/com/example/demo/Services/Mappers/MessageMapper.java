package com.example.demo.Services.Mappers;

import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.Message;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Exceptions.Models.GroupNotFoundException;
import com.example.demo.Exceptions.Models.UserNotFoundException;
import com.example.demo.Globals.GlobalLogger;
import com.example.demo.Services.DBServices.GroupService;
import com.example.demo.Services.DBServices.UserService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {
    private static final Logger logger = GlobalLogger.getLogger(MessageMapper.class);

    private final UserService userService;
    private final GroupService groupService;

    public MessageMapper(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    public MessageDTO toDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getContent(),
                message.getSender().getUsername(),
                message.getSentAt(),
                message.getEvent().getId()
        );
    }
}
