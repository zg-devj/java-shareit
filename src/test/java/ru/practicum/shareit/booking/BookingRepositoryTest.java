package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Slf4j
@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookingRepositoryTest {

    private final TestEntityManager tem;
    private final BookingRepository bookingRepository;

    private final LocalDateTime now = LocalDateTime.now();

    private User booker;
    private User addedBooker;
    private User owner;
    private User addedOwner;
    private Item item;
    private Item addedItem;
    private Booking booking;
    private Booking addedBooking;
    private Booking booking2;
    private Booking addedBooking2;

    @BeforeEach
    void setUp() {
        booker = User.builder()
                .name("booker").email("booker@example.com")
                .build();
        addedBooker = tem.persistAndFlush(booker);
        owner = User.builder()
                .name("owner").email("owner@example.com")
                .build();
        addedOwner = tem.persistAndFlush(owner);
        item = Item.builder()
                .name("молоток")
                .description("стальной молоток")
                .available(true)
                .owner(addedOwner)
                .build();
        addedItem = tem.persistAndFlush(item);
        booking = Booking.builder()
                .start(now.minusDays(4))
                .end(now.minusDays(3))
                .booker(addedBooker)
                .item(addedItem)
                .status(BookingStatus.APPROVED)
                .build();
        addedBooking = tem.persistAndFlush(booking);
        booking2 = Booking.builder()
                .start(now.plusDays(2))
                .end(now.plusDays(4))
                .booker(addedBooker)
                .item(addedItem)
                .status(BookingStatus.APPROVED)
                .build();
        addedBooking2 = tem.persistAndFlush(booking);
    }


    @Test
    void test_findBookingForComment() {

        PageRequest pager = PageRequest.of(0, 1);

        Page<Booking> returned = bookingRepository.findBookingForComment(1L, 1L, now, BookingStatus.APPROVED, pager);
        Booking bookingReturned = returned.get().findFirst().orElse(null);

        Assertions.assertThat(bookingReturned).isNotNull()
                .isEqualTo(addedBooking);
    }

    @Test
    void test_findBookingByOwnerOrBooker() {

        // By Booker
        Booking bookingReturned = bookingRepository
                .findBookingByOwnerOrBooker(2L, addedBooker.getId()).orElse(null);
        Assertions.assertThat(bookingReturned).isNotNull()
                .isEqualTo(addedBooking2);

        // By Owner
        Booking bookingReturned2 = bookingRepository
                .findBookingByOwnerOrBooker(2L, owner.getId()).orElse(null);
        Assertions.assertThat(bookingReturned2).isNotNull()
                .isEqualTo(addedBooking2);

        // Wrong BookingId
        Booking bookingReturned3 = bookingRepository
                .findBookingByOwnerOrBooker(99L, owner.getId()).orElse(null);
        Assertions.assertThat(bookingReturned3).isNull();

        // Wrong BookerId or OwnerId
        Booking bookingReturned4 = bookingRepository
                .findBookingByOwnerOrBooker(1L, 99L).orElse(null);
        Assertions.assertThat(bookingReturned4).isNull();
    }
}