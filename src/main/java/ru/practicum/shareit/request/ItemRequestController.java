package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.Utils;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.utils.Utils.userIsNull;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestDto requestDto
    ) {
        log.info("POST /requests - добавление запроса на вещь пользователем {}", userId);
        userIsNull(userId);
        ItemRequestDto saved = service.saveItemRequest(userId, requestDto);
        return saved;
    }

    @GetMapping
    public List<ItemRequestDto> findAllByRequestor(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("GET /requests - список запросов вещей пользователя {}", userId);
        userIsNull(userId);
        List<ItemRequestDto> itemRequestDtos = service.findAllByRequestor(userId);
        return itemRequestDtos;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "0") int from,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        log.info("GET /requests/all - список запросов");
        Utils.checkPaging(from, size);
        List<ItemRequestDto> itemRequestDtos = service.findItemRequests(userId, from, size);
        return itemRequestDtos;
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long id
    ) {
        log.info("GET /requests/{} - информация о запросе", id);
        userIsNull(userId);
        ItemRequestDto itemRequestDto = service.getItemRequest(userId, id);
        return itemRequestDto;
    }
}
