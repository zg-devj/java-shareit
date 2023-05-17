package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
public class BookingShort {
    private Long id;
    private Long bookerId;

    public BookingShort(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
