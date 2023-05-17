package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShort;

@Data
@Builder
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
}
