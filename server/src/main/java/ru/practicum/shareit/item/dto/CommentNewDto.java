package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Dto для создания комментария
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentNewDto {
    private String text;
}
