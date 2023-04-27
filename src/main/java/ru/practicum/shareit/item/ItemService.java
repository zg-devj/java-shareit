package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public interface ItemService {
    Item saveItem(Long userId, Item item);
}
