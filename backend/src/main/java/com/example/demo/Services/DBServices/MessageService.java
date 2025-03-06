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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    public Message saveMessage(String content, Long eventId, User sender){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("No Event found"));

        validateMessagingWindow(event);

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setEvent(event);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<MessageDTO> getMessagesForEvent(Long eventId){
        return messageRepository.findAllByEventId(eventId)
                .stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }
    private void validateMessagingWindow(Event event) {
        // Get the current date
        LocalDate now = LocalDate.now();

        // Format the current date as yyyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedNow = now.format(formatter);

        // Convert the formatted string back to LocalDate (if needed)
        LocalDate formattedNowDate = LocalDate.parse(formattedNow, formatter);

        // Get the event date
        LocalDate eventDate = LocalDate.from(event.getDate());

        if (!formattedNowDate.isAfter(eventDate.plusDays(2))) {
            throw new IllegalStateException("Messaging closed for this event");
        }
    }
}
