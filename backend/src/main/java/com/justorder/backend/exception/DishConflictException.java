package com.justorder.backend.exception;

public class DishConflictException extends RuntimeException {

    public DishConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}