package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item saveItem(Long userId, Item item) {
        User user = userService.findUserById(userId);
        item.setOwner(user);
        Item saved = itemRepository.save(item);
        log.info("Пользователь id={} добавил вещь с id={}", userId, saved.getId());
        return saved;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        User user = userService.findUserById(userId);

        Item updated = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", item.getId())));

        if (!Objects.equals(user.getId(), updated.getOwner().getId())) {
            // Вещь не является вещью пользователя
            throw new ForbiddenException("У вас нет прав для редактирования.");
        }
        if (item.getName() != null) {
            log.info("Обновляется имя вещи с id={}.", updated.getId());
            updated.setName(item.getName());
        }
        if (item.getDescription() != null) {
            log.info("Обновляется описание вещи с id={}.", updated.getId());
            updated.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            log.info("Обновляется статус одобрения вещи с id={}.", updated.getId());
            updated.setAvailable(item.getAvailable());
        }
        log.info("Пользователь id={} обновил вещь с id={}", userId, updated.getId());
        return itemRepository.update(updated);
    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemId)));
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        return itemRepository.findAllByUserId(userId);
    }

    @Override
    public List<Item> search(String search) {
        return itemRepository.findByNameAndByDescription(search);
    }
}
