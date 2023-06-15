package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? ItemMapper.itemToDto(itemRequest.getItems()) : new ArrayList<>())
                .build();
    }

    public static List<ItemRequestDto> itemRequestToDto(List<ItemRequest> requestList) {
        return requestList.stream()
                .map(ItemRequestMapper::itemRequestToDto)
                .collect(Collectors.toList());
    }

    public static List<ItemRequestDto> itemRequestToDto(List<ItemRequest> requestList, List<Item> items) {
        for (ItemRequest itemRequest : requestList) {
            itemRequest.setItems(items.stream()
                    .filter(f -> Objects.equals(f.getRequest().getId(), itemRequest.getId()))
                    .collect(Collectors.toList()));
        }
        return ItemRequestMapper.itemRequestToDto(requestList);
    }
}
