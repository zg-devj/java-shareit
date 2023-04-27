package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;


@Repository
public class ItemInMemoryRepository implements ItemRepository {
    Map<Long, Item> items = new HashMap<>();
    private Long identity = 0L;

    @Override
    public Item save(Item item) {
        item.setId(++identity);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }
}
