package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {


    public static Booking toBooking(BookingNewDto bookingNewDto, Item item, User user) {

        return Booking.builder()
                .booker(user)
                .item(item)
                .start(bookingNewDto.getStart())
                .end(bookingNewDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart().format(formatter))
                .end(booking.getEnd().format(formatter))
                .status(booking.getStatus().name())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }
}

