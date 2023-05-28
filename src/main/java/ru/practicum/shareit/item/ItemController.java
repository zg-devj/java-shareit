package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Utils;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utils.Utils.userIsNull;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentNewDto commentNewDto
    ) {
        log.info("POST /items/{}/comment - добавление комментария пользователем {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentNewDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        userIsNull(userId);
        log.info("POST /items - добавление вещи пользователем {}", userId);
        return itemService.saveItem(userId, itemDto);
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
    public ItemBookingDto findById(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        userIsNull(userId);
        log.info("GET /items/{} - просмотр вещи", id);
        return itemService.findById(id, userId);
    }

    @GetMapping
    public List<ItemBookingDto> findAllByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        userIsNull(userId);
        Utils.checkPaging(from, size);
        log.info("GET /items - просмотр вещей пользователем с id={}", userId);
        return itemService.findAllByUserId(userId, from, size);
    }


    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        Utils.checkPaging(from, size);
        return itemService.search(text, from, size);
    }
}
