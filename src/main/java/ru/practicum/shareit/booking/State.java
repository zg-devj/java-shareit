package ru.practicum.shareit.booking;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public boolean equal(String paramState) {
        return this.name().equals(paramState.toUpperCase());
    }
}
