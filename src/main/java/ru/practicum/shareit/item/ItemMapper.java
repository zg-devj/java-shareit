package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Item item, List<BookingShort> list) {
        BookingShort last = null;
        BookingShort next = null;
        if(list.size()==1){
            last = new BookingShort(list.get(0).getId(),list.get(0).getBookerId());
        }
        if(list.size()==2){
            last = new BookingShort(list.get(0).getId(),list.get(0).getBookerId());
            next = new BookingShort(list.get(1).getId(),list.get(1).getBookerId());
        }
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(last)
                .nextBooking(next)
                .build();
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        return StreamSupport.stream(items.spliterator(), false)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
