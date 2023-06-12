package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.exceptions.BadRequestException;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplValidDateTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingNewDto bookingNewDto;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        bookingNewDto = BookingNewDto.builder()
                .itemId(1L)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .build();
    }

    //region Valid date
    @Test
    void createBooking_validDate_StartNull_ReturnBadRequestException() {
        bookingNewDto.setStart(null);
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата начала бронирования должны быть указано.");
    }

    @Test
    void createBooking_validDate_EndNull_ReturnBadRequestException() {
        bookingNewDto.setEnd(null);
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата окончания бронирования должны быть указано.");
    }

    @Test
    void createBooking_validDate_StartEqualEnd_ReturnBadRequestException() {
        bookingNewDto.setStart(now);
        bookingNewDto.setEnd(now);
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата начала бронирования не должна совпадать " +
                        "с датой окончания бронирования.");
    }

    @Test
    void createBooking_validDate_StartBeforeNow_ReturnBadRequestException() {
        bookingNewDto.setStart(now.minusDays(2));
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата начала бронирования не должна быть " +
                        "в прошлом.");
    }

    @Test
    void createBooking_validDate_EndBeforeNow_ReturnBadRequestException() {
        bookingNewDto.setEnd(now.minusDays(2));
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата окончания бронирования не должна быть " +
                        "в прошлом.");
    }

    @Test
    void createBooking_validDate_StartAfterEnd_ReturnBadRequestException() {
        bookingNewDto.setStart(now.plusDays(3));
        bookingNewDto.setEnd(now.plusDays(2));
        Throwable throwable = Assertions.catchException(() -> bookingService.createBooking(1L, bookingNewDto));

        Assertions.assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Дата начала бронирования не должна быть " +
                        "позже даты окончания бронирования.");
    }
    //endregion
}