package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;

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
        log.info("POST /bookings - создание бронирования пользователем {}.", userId);
        return bookingService.createBooking(userId, bookingNewDto);
    }

    @PatchMapping(value = "/{bookingId}", params = "approved")
    public BookingDto approve(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{} - разрешение/отмена бронирования.", bookingId);
        return bookingService.approve(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{} - запрос бронирования.", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAll(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /bookings - все бронирования.");
        return bookingService.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAll(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /bookings/owner - все бронирования владельца.");
        return bookingService.getAllBookingsForOwner(userId, state, from, size);
    }
}
