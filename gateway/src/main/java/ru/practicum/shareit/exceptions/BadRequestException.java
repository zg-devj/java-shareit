package ru.practicum.shareit.exceptions;

// для кода 400
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
