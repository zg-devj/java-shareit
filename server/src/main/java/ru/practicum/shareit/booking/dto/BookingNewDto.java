package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class BookingNewDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
