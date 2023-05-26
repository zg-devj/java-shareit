package ru.practicum.shareit.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    /**
     * Если id пользователя null, то вызываем исключение BadRequestException
     *
     * @param userId id пользователя
     */
    public static void userIsNull(Long userId) {
        if (userId == null) throw new BadRequestException("Не известен пользователь.");
    }

    /**
     * формат времени
     * yyyy-MM-dd'T'HH:mm:ss
     */
    public static final DateTimeFormatter dtFormatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");
}
