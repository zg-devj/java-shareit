package ru.practicum.shareit.exceptions;

// для кода 404
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
