package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {


    public static Booking toBooking(BookingNewDto bookingNewDto, Item item, User user) {

        return Booking.builder()
                .booker(user)
                .item(item)
                //.start(bookingNewDto.getStart().toInstant(ZoneOffset.UTC))
                //.end(bookingNewDto.getEnd().toInstant(ZoneOffset.UTC))
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto dto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().toString())
                .end(booking.getEnd().toString())
                .status(booking.getStatus().name())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
        return dto;
    }

    public static Instant convertStringToInstant(String date) {
        String pattern = "yyyy-MM-ddThh:mm:ss";
        return LocalDateTime.parse(date,
                DateTimeFormatter.ofPattern(pattern)).toInstant(ZoneOffset.UTC);
    }

    public static String convertDateToString(LocalDateTime date) {
        String pattern = "yyyy-MM-ddThh:mm:ss";
        return DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneOffset.UTC)
                .format(date);
    }
}
