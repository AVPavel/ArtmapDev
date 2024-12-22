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

    public Message toEntity(MessageDTO messageDTO) {
        Message message = new Message();

        message.setContent(messageDTO.getContent());
        try {
            User user = userService.getUserByUsername(messageDTO.getSender());
            message.setSender(user);
        } catch (UserNotFoundException e) {
            logger.error("MessageMapper toEntity(): {}", e.getMessage());
            return null;
        }

        try {
            Group group = groupService.getGroupByEventId(messageDTO.getEventId());
            message.setGroup(group);
        } catch(GroupNotFoundException ex) {
            logger.error("MessageMapper toEntity(): {}", ex.getMessage());
            return null;
        }
        message.setSentAt(messageDTO.getTimestamp());
        message.setReadByAll(false);

        return message;
    }

    public MessageDTO toDTO(Message message){
        MessageDTO dto = new MessageDTO();

        dto.setContent(message.getContent());
        dto.setSender(message.getSender().getUsername());
        dto.setTimestamp(message.getSentAt());
        dto.setEventId(message.getGroup().getEvent().getId());

        return dto;
    }
}
