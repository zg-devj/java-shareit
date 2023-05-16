package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class BookingNewDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
