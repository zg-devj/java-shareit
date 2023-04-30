package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Repository
public class ItemInMemoryRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long identity = 0L;

    @Override
    public Item save(Item item) {
        item.setId(++identity);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return items.values().stream()
                .filter(u -> Objects.equals(u.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByNameAndByDescription(String search) {
        if (search.isBlank()) {
            return new ArrayList<>();
        }
        String searchLow = search.toLowerCase();
        Predicate<Item> predicate1 = s -> s.getName().toLowerCase().contains(searchLow);
        Predicate<Item> predicate2 = s -> s.getDescription().toLowerCase().contains(searchLow);
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(predicate1.or(predicate2))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        List<Long> idS = items.values().stream()
                .filter(u -> Objects.equals(u.getOwner().getId(), userId))
                .map(Item::getId)
                .collect(Collectors.toList());
        for (Long id : idS) {
            items.remove(id);
        }
    }
}
