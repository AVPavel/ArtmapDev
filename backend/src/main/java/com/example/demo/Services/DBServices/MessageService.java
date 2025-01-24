package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.Message;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.GroupRepository;
import com.example.demo.Repositories.MessageRepository;
import com.example.demo.Services.Mappers.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final MessageMapper messageMapper;

    @Autowired
    public MessageService(
            MessageRepository messageRepository,
            EventRepository eventRepository,
            GroupRepository groupRepository,
            MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.eventRepository = eventRepository;
        this.groupRepository = groupRepository;
        this.messageMapper = messageMapper;
    }

    public Message saveMessage(String content, Long eventId, User sender){
        Group group = groupRepository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException("No Group found for event"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("No Event found"));
        if(!isEventOpenForMessaging(event)){
            throw new IllegalStateException("Event is not open for Messaging");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setGroup(group);
        message.setSentAt(LocalDateTime.now());
        message.setReadByAll(false);

        return messageRepository.save(message);
    }

    public List<MessageDTO> getMessagesForEvent(Long eventId){

        List<Message> messages = messageRepository.findAllMessagesByEventId(eventId);

        return messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }

    private boolean isEventOpenForMessaging(Event event){
        LocalDateTime now = LocalDateTime.now();
        return !now.isAfter(event.getDate().plusDays(2));
    }
}
