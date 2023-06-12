package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    ItemBookingDto findById(Long itemId, Long userId);

    List<ItemBookingDto> findAllByUserId(Long userId, int from, int size);

    List<ItemDto> search(String search, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentNewDto comment);
}
