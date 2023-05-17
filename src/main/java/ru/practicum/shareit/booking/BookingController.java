package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

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
            @RequestParam boolean approved
    ) {
        return bookingService.approve(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAll(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        State state1;
        try {
            state1 = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }

        return bookingService.getAllBookings(userId, state1, false);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        State state1;
        try {
            state1 = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
        return bookingService.getAllBookings(userId, state1, true);
    }

    @Transactional
    @GetMapping("/info")
    public List<Booking> info() {
        List<Booking> bookings = bookingRepository.findAllByItem_OwnerId(4L);
        return bookings;
    }
}
