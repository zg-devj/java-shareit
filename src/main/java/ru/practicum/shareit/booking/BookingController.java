package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.utils.Utils;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        State state1 = checkState(state);
        Utils.checkPaging(from, size);

        log.info("GET /bookings - все бронирования");
        return bookingService.getAllBookings(userId, state1, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        State state1 = checkState(state);
        Utils.checkPaging(from, size);

        log.info("GET /bookings/owner - все бронирования владельца");
        return bookingService.getAllBookingsForOwner(userId, state1, from, size);
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
