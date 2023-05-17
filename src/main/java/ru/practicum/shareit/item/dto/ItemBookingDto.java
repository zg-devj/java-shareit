package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShort;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
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
