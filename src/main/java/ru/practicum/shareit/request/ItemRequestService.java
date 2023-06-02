package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> findAllByRequestor(long userId);

    List<ItemRequestDto> findItemRequests(long userId, int from, int size);

    ItemRequestDto getItemRequest(long userId, long itemRequestId);
}
