package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

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
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "0") int size
    ) {
        log.info("GET /requests/all - список запросов");
        return null;
    }
}
