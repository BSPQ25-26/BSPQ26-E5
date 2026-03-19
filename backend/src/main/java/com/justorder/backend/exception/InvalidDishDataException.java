package com.justorder.backend.exception;

public class InvalidDishDataException extends RuntimeException {

    public InvalidDishDataException(String message) {
        super(message);
    }
}