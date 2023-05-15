package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto,
            HttpServletResponse response
    ) {
        userIsNull(userId);
        log.info("POST /items - добавление вещи пользователем {}", userId);
        ItemDto created = itemService.saveItem(userId, itemDto);
        response.setStatus(201);
        return created;
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long id
    ) {
        userIsNull(userId);
        log.info("PATCH /items/{} - обновить вещь", id);
        itemDto.setId(id);
        return itemService.updateItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto findById(
            @PathVariable Long id
    ) {
        log.info("GET /items/{} - просмотр вещи", id);
        return itemService.findById(id);
    }

    @GetMapping
    public List<ItemDto> findAllByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId
    ) {
        userIsNull(userId);
        log.info("GET /items - просмотр вещей пользователем с id={}", userId);
        return itemService.findAllByUserId(userId);
    }


    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text
    ) {
        return itemService.search(text);
    }

    private void userIsNull(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Не известен пользователь.");
        }
    }
}
