package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;

import static ru.practicum.shareit.utils.Utils.checkPaging;
import static ru.practicum.shareit.utils.Utils.userIsNull;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemRequestDto itemDto
    ) {
        userIsNull(userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestBody ItemRequestDto itemDto,
            @PathVariable long id
    ) {
        userIsNull(userId);
        return itemClient.update(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        userIsNull(userId);
        return itemClient.findById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        userIsNull(userId);
        checkPaging(from, size);
        return itemClient.findAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        checkPaging(from, size);
        return itemClient.search(text, from, size);
    }
}
