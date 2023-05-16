package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody BookingNewDto bookingNewDto
    ) {
        return bookingService.createBooking(userId, bookingNewDto);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public BookingDto approve(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam(required = true) boolean approved
    ) {
        return bookingService.approve(userId, approved, bookingId);
    }

}
