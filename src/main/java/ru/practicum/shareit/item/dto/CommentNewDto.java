package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// Dto для создания комментария
@Getter
@NoArgsConstructor
public class CommentNewDto {
    private String text;
}
