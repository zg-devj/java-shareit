package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    ItemDto findById(Long itemId);

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> search(String search);
}
