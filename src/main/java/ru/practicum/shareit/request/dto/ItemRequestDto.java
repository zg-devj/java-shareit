package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotEmpty
    @Size(max = 255)
    private String description;

    private String created;

    @Setter
    private List<ItemDto> items = new ArrayList<>();
}
