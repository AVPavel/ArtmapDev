package com.example.demo.Exceptions.Models;

public class GroupNotFoundException extends EntityNotFoundException {
    public GroupNotFoundException(String message) {
        super(message,"User");
    }
}
