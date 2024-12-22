package com.example.demo.Exceptions.Models;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String message) {
        super(message,"User");
    }
}
