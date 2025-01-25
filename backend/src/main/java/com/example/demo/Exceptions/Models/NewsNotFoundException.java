package com.example.demo.Exceptions.Models;

public class NewsNotFoundException extends EntityNotFoundException {
    public NewsNotFoundException(String message) {
        super(message,"News");
    }
}
