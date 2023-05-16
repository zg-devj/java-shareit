package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingNewDto bookingNewDto);
}
