package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    private LocalDateTime created;

    // TODO: 01.06.2023 Delete
//    @Setter
    private List<ItemDto> items = new ArrayList<>();
}
