package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest dtoToItemRequest(User user, ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .requestor(user)
                .description(requestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated().format(Utils.dtFormatter))
                .build();
    }
}
