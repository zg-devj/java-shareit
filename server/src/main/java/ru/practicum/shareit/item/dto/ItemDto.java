package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    @Setter
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
