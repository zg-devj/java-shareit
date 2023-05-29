package ru.practicum.shareit.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.BadRequestException;

class UtilsTest {

    private static final String PAGING_ERROR_MESSAGE = "Не верный запрос для постраничного вывода.";

    @Test
    void userIsNull_Normal_ReturnBadRequest() {
        String message = "Не известен пользователь.";

        Throwable throwable = Assertions.catchException(() -> Utils.userIsNull(null));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(message);
    }

    @Test
    void checkPaging_Normal() {
        Assertions.assertThatNoException().isThrownBy(() -> Utils.checkPaging(1,1));
        Assertions.assertThatNoException().isThrownBy(() -> Utils.checkPaging(0,1));
    }

    @Test
    void checkPaging_FromZeroAndSizeZero_ReturnBadRequest() {
        Throwable throwable = Assertions.catchException(() -> Utils.checkPaging(0, 0));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PAGING_ERROR_MESSAGE);
    }

    @Test
    void checkPaging_FromLessZeroAndSizeMoreZero_ReturnBadRequest() {
        Throwable throwable = Assertions.catchException(() -> Utils.checkPaging(-1, 1));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PAGING_ERROR_MESSAGE);
    }

    @Test
    void checkPaging_FromZeroAndSizeLessZero_ReturnBadRequest() {
        Throwable throwable = Assertions.catchException(() -> Utils.checkPaging(0, -1));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(PAGING_ERROR_MESSAGE);
    }
}