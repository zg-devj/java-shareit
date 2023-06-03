package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    private final TestEntityManager tem;
    private final BookingRepository bookingRepository;

    private final LocalDateTime now = LocalDateTime.now();

    private User owner;
    private Item item;
    private User booker;
    private Booking booking;

    @BeforeEach
    void setUp() {
        init();
    }

    private void init() {
        owner = User.builder().name("owner").email("owner@example.com").build();
        tem.persist(owner);
        item = Item.builder()
                .name("молоток").description("стальной молоток").available(true)
                .owner(owner).build();
        tem.persist(item);
        booker = User.builder().name("booker").email("booker@example.com").build();
        tem.persist(booker);
    }

    private void init_BookingPastApproved() {
        booking = Booking.builder()
                .item(item).booker(booker)
                .start(now.minusDays(5)).end(now.minusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(booking);
    }

    private void init_BookingFutureApproved() {
        booking = Booking.builder()
                .item(item).booker(booker)
                .start(now.plusDays(3)).end(now.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(booking);
    }

    @Test
    void test_findBookingForComment_Normal() {
        init_BookingPastApproved();

        PageRequest page = PageRequest.of(0, 1);

        Page<Booking> finder = bookingRepository.findBookingForComment(booker.getId(), item.getId(), now, BookingStatus.APPROVED,
                page);
        Assertions.assertThat(finder.get().findFirst().orElse(null)).isNotNull().isEqualTo(booking);
    }

    @Test
    void test_findBookingForComment_WringBookingId() {
        init_BookingPastApproved();

        PageRequest page = PageRequest.of(0, 1);

        Page<Booking> finder2 = bookingRepository.findBookingForComment(999L, item.getId(), now, BookingStatus.APPROVED,
                page);
        Assertions.assertThat(finder2.get().findFirst().orElse(null)).isNull();
    }

    @Test
    void test_findBookingByOwnerOrBooker_ByOwner() {
        init_BookingFutureApproved();

        // By Owner
        Booking bookingReturned = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), owner.getId()).orElse(null);

        Assertions.assertThat(bookingReturned).isNotNull()
                .isEqualTo(booking);
    }

    @Test
    void test_findBookingByOwnerOrBooker_ByBooker() {
        init_BookingFutureApproved();

        // By Booker
        Booking bookingReturned2 = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), booker.getId()).orElse(null);

        Assertions.assertThat(bookingReturned2).isNotNull()
                .isEqualTo(booking);
    }

    @Test
    void test_findBookingByOwnerOrBooker_WrongBookingId() {
        init_BookingFutureApproved();

        // Wrong BookingId
        Booking bookingReturned3 = bookingRepository
                .findBookingByOwnerOrBooker(99L, owner.getId()).orElse(null);

        Assertions.assertThat(bookingReturned3).isNull();
    }

    @Test
    void test_findBookingByOwnerOrBooker_WrongBookerIdOrOwnerId() {
        init_BookingFutureApproved();

        // Wrong BookerId or OwnerId
        Booking bookingReturned4 = bookingRepository
                .findBookingByOwnerOrBooker(booking.getId(), 99L).orElse(null);

        Assertions.assertThat(bookingReturned4).isNull();
    }

    @Test
    void test_findBookingForApprove_Correct() {
        init_BookingFutureApproved();

        Booking bookingRet = bookingRepository
                .findBookingForApprove(booking.getId(), owner.getId()).orElse(null);
        Assertions.assertThat(bookingRet).isNotNull()
                .isEqualTo(booking);
    }

    @Test
    void test_findBookingForApprove_WrongOwner() {
        init_BookingFutureApproved();

        Booking bookingRet2 = bookingRepository
                .findBookingForApprove(booking.getId(), 99L).orElse(null);
        Assertions.assertThat(bookingRet2).isNull();
    }

    @Test
    void test_getLastBooking() {
        User bookerA = User.builder().name("bookerA").email("bookerA@example.com").build();
        tem.persist(bookerA);
        Booking bookingA = Booking.builder()
                .item(item).booker(bookerA)
                .start(now.minusDays(3)).end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(bookingA);
        User bookerB = User.builder().name("bookerB").email("bookerB@example.com").build();
        tem.persist(bookerB);
        Booking bookingB = Booking.builder()
                .item(item).booker(bookerB)
                .start(now.plusDays(2)).end(now.plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(bookingB);

        PageRequest page = PageRequest.of(0, 1);

        BookingShort last = bookingRepository.getLastBooking(item.getId(), owner.getId(), BookingStatus.APPROVED,
                now, page).get().findFirst().orElse(null);
        Assertions.assertThat(last).isNotNull()
                .hasFieldOrPropertyWithValue("id", bookingA.getId())
                .hasFieldOrPropertyWithValue("bookerId", bookerA.getId());
    }

    @Test
    void test_getNextBooking() {
        User bookerA = User.builder().name("bookerA").email("bookerA@example.com").build();
        tem.persist(bookerA);
        Booking bookingA = Booking.builder()
                .item(item).booker(bookerA)
                .start(now.minusDays(3)).end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(bookingA);
        User bookerB = User.builder().name("bookerB").email("bookerB@example.com").build();
        tem.persist(bookerB);
        Booking bookingB = Booking.builder()
                .item(item).booker(bookerB)
                .start(now.plusDays(2)).end(now.plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        tem.persist(bookingB);

        PageRequest page = PageRequest.of(0, 1);

        BookingShort next = bookingRepository.getNextBooking(item.getId(), owner.getId(), BookingStatus.APPROVED,
                now, page).get().findFirst().orElse(null);
        Assertions.assertThat(next).isNotNull()
                .hasFieldOrPropertyWithValue("id", bookingB.getId())
                .hasFieldOrPropertyWithValue("bookerId", bookerB.getId());

    }
}