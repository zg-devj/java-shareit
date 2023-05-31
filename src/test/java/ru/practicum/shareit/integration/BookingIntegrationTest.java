package ru.practicum.shareit.integration;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingIntegrationTest {

    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void test_getAllBookingsForOwner() {
        LocalDateTime now = LocalDateTime.now();

        //region init
        User owner = User.builder().name("owner").email("owner@example.com").build();
        em.persist(owner);
        User booker = User.builder().name("booker").email("booker@example.com").build();
        em.persist(booker);
        Item item1 = Item.builder()
                .name("молоток").description("хороший молоток").available(true)
                .owner(owner).build();
        em.persist(item1);
        Item item2 = Item.builder()
                .name("дрель").description("мощная дрель").available(true)
                .owner(owner).build();
        em.persist(item2);
        // current approved
        Booking booking1 = Booking.builder()
                .item(item1).booker(booker).status(BookingStatus.APPROVED)
                .start(now.minusDays(1)).end(now.plusDays(1))
                .build();
        em.persist(booking1);
        // future waiting
        Booking booking2 = Booking.builder()
                .item(item1).booker(booker).status(BookingStatus.WAITING)
                .start(now.plusDays(1)).end(now.plusDays(2))
                .build();
        em.persist(booking2);
        // past
        Booking booking3 = Booking.builder()
                .item(item2).booker(booker).status(BookingStatus.REJECTED)
                .start(now.minusDays(2)).end(now.minusDays(1))
                .build();
        em.persist(booking3);
        // current canceled
        Booking booking4 = Booking.builder()
                .item(item2).booker(booker).status(BookingStatus.CANCELED)
                .start(now.minusDays(1)).end(now.plusDays(1))
                .build();
        em.persist(booking4);
        //endregion

        List<BookingDto> list1 = bookingService.getAllBookings(booker.getId(), State.ALL, 0, 20);
        Assertions.assertThat(list1).isNotEmpty().hasSize(4);

        List<BookingDto> list2 = bookingService.getAllBookings(booker.getId(), State.PAST, 0, 20);
        Assertions.assertThat(list2).isNotEmpty().hasSize(1);

        List<BookingDto> list3 = bookingService.getAllBookings(booker.getId(), State.FUTURE, 0, 20);
        Assertions.assertThat(list3).isNotEmpty().hasSize(1);

        List<BookingDto> list4 = bookingService.getAllBookings(booker.getId(), State.CURRENT, 0, 20);
        Assertions.assertThat(list4).isNotEmpty().hasSize(2);

        List<BookingDto> list5 = bookingService.getAllBookings(booker.getId(), State.REJECTED, 0, 20);
        Assertions.assertThat(list5).isNotEmpty().hasSize(2);

        List<BookingDto> list6 = bookingService.getAllBookings(booker.getId(), State.WAITING, 0, 20);
        Assertions.assertThat(list6).isNotEmpty().hasSize(1);
    }
}
