package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestNewDto {

//    private Long id;

    @NotEmpty
    @Size(max = 255)
    private String description;

    private LocalDateTime created;

    // TODO: 12.06.2023 DELETE
//    @Setter
//    private List<ItemDto> items = new ArrayList<>();
}
