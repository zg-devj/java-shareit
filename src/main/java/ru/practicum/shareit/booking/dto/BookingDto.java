package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDate;

@Builder
@Getter
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private String status;
    private UserDto booker;
    private ItemDto item;
}
