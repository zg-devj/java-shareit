package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveItemRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> findAllByRequestor(Long userId);

    List<ItemRequestDto> findItemRequests(int from, int size);
}
