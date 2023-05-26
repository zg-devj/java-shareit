package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exceptions.BadRequestException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckUtil {

    public static void userIsNull(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Не известен пользователь.");
        }
    }
}
