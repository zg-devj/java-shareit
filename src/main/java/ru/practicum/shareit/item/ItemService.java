package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Item findById(Long itemId);

    List<Item> findAllByUserId(Long userId);

    List<Item> search(String search);
}
