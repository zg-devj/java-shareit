package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
            BindingResult result,
            HttpServletResponse response
    ) {
        if (result.hasErrors()) {
            Optional<FieldError> field = result.getFieldErrors().stream()
                    .filter(f -> f.getObjectName().equals("itemDto")).findFirst();
            if (field.isPresent()) {
                throw new BadRequestException(field.get().getDefaultMessage());
            }
        }
        if (userId == null) {
            throw new BadRequestException("Не известен польтватель.");
        }
        log.info("POST /items - добавление вещи пользователем {}", userId);
        Item item = ItemMapper.toItem(itemDto);
        Item created = itemService.saveItem(userId, item);
        response.setStatus(201);
        return ItemMapper.toItemDto(created);
    }
}
