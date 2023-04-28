package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @Valid @RequestBody ItemDto itemDto,
            HttpServletResponse response
    ) {
        userIsNull(userId);
        log.info("POST /items - добавление вещи пользователем {}", userId);
        Item item = ItemMapper.toItem(itemDto);
        Item created = itemService.saveItem(userId, item);
        response.setStatus(201);
        return ItemMapper.toItemDto(created);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long id
    ) {
        userIsNull(userId);
        log.info("PATCH /items/{} - обновить вещь", id);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(id);
        Item created = itemService.updateItem(userId, item);
        return ItemMapper.toItemDto(created);
    }

    @GetMapping("/{id}")
    public ItemDto findById(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable Long id
    ) {
        log.info("GET /items/{} - просмотр вещи", id);
        Item item = itemService.findById(id);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId
    ) {
        userIsNull(userId);
        log.info("GET /items - просмотр вещей пользователем с id={}", userId);
        List<Item> items = itemService.findAllByUserId(userId);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text
    ) {
        List<Item> items = itemService.search(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void userIsNull(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Не известен пользователь.");
        }
    }
}
