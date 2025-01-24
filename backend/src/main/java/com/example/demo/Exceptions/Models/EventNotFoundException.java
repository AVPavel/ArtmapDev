package com.example.demo.Exceptions.Models;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(String message) {
        super(message,"Event");
    }
}
