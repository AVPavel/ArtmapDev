package com.example.demo.Exceptions.Models;

public class CategoryNotFoundException extends EntityNotFoundException {
    public CategoryNotFoundException(String message) {
        super(message,"Category");
    }
}
