package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым или отсутствовать.")
    @Size(max = 100, message = "Название не должна быть больше 100 символов.")
    private String name;

    @NotBlank(message = "Описание не может быть пустым или отсутствовать.")
    @Size(max = 200, message = "Описание не должна быть больше 200 символов.")
    private String description;

    private Boolean available;
}
