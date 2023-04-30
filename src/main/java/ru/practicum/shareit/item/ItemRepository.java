package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findAllByUserId(Long userId);

    List<Item> findByNameAndByDescription(String search);

    void deleteAllByUserId(Long userId);
}
