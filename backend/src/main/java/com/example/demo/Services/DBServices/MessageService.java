package com.example.demo.Services.DBServices;

import com.example.demo.DBModels.Event;
import com.example.demo.DBModels.Group;
import com.example.demo.DBModels.Message;
import com.example.demo.DBModels.User;
import com.example.demo.DTOs.Messages.MessageDTO;
import com.example.demo.Exceptions.Models.GroupNotFoundException;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.GroupRepository;
import com.example.demo.Repositories.MessageRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Services.Mappers.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final MessageMapper messageMapper;
    private final EventService eventService;

    @Autowired
    public MessageService(
            MessageRepository messageRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            GroupRepository groupRepository,
            MessageMapper messageMapper, EventService eventService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.groupRepository = groupRepository;
        this.messageMapper = messageMapper;
        this.eventService = eventService;
    }

    public Message saveMessage(String content, Long eventId, User sender){
        //Ar trebuie sa gasesc grupul aferent evenimentului
        Group group = groupRepository.findByEventId(eventId)
                .orElseThrow(() -> new IllegalArgumentException("No Group found for event"));
        //ar trebui sa validez faptul ca se mai poate trimite mesajul (functie separata)
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("No Event found"));
        if(!isEventOpenForMessaging(event)){
            throw new IllegalStateException("Event is not open for Messaging");
        }
        //creez mesajul
        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setGroup(group);
        message.setSentAt(LocalDateTime.now());
        message.setReadByAll(false);
        //salvez

        return messageRepository.save(message);
    }

    public List<MessageDTO> getMessagesForEvent(Long eventId){

        Event event = eventService.getEventById(eventId);

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
