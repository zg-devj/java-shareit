package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto saveItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Пользователь c id=%d не найдена", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item saved = itemRepository.save(item);
        log.info("Пользователь id={} добавил вещь с id={}", userId, saved.getId());
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto) {

        Item updated = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemDto.getId())));

        User user = updated.getOwner();

        if (!Objects.equals(user.getId(), userId)) {
            // Вещь не является вещью пользователя
            throw new ForbiddenException("У вас нет прав для редактирования.");
        }
        if (itemDto.getName() != null) {
            log.info("Обновляется имя вещи с id={}.", updated.getId());
            updated.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Обновляется описание вещи с id={}.", updated.getId());
            updated.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Обновляется статус одобрения вещи с id={}.", updated.getId());
            updated.setAvailable(itemDto.getAvailable());
        }
        log.info("Пользователь id={} обновил вещь с id={}", userId, updated.getId());
        return ItemMapper.toItemDto(itemRepository.save(updated));
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Вещь c id=%d не найдена", itemId)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        return ItemMapper.toItemDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemDto> search(String search) {
        if(search!=null && search.isBlank()){
            return new ArrayList<>();
        }
        return ItemMapper.toItemDto(
                itemRepository.search(search));
    }
}
