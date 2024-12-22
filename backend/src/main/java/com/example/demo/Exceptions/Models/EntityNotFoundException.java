package com.example.demo.Exceptions.Models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    public EntityNotFoundException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }
}