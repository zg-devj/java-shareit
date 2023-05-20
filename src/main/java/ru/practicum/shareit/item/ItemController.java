package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

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
        CommentDto result = itemService.addComment(userId, itemId, commentNewDto);
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        userIsNull(userId);
        log.info("POST /items - добавление вещи пользователем {}", userId);
        ItemDto created = itemService.saveItem(userId, itemDto);
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
