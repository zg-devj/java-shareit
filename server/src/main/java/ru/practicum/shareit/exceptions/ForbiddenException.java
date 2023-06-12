package ru.practicum.shareit.exceptions;

// для кода 403 (Forbidden)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
