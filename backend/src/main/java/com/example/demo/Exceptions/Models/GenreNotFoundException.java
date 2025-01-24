package com.example.demo.Exceptions.Models;

public class GenreNotFoundException extends EntityNotFoundException {

    public GenreNotFoundException(String message) {
        super(message, "Genre");
    }
}
