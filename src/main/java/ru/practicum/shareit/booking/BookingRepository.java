package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // проверяем, было ли бронирование вещи пользователем
    @Query("select b from Booking as b " +
            "where b.booker.id=?1 and b.item.id=?2 and " +
            "b.end<?3 and b.status=?4 order by b.end asc ")
    Page<Booking> findBookingForComment(Long bookerId, Long itemId,
                                        LocalDateTime now, BookingStatus status, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "where b.id=?1 and( b.booker.id=?2 or b.item.owner.id=?2 )")
    Optional<Booking> findBookingByOwnerOrBooker(Long id, Long finderId);

    @Query("select b from Booking as b where b.id=?1 and b.item.owner.id=?2")
    Optional<Booking> findBookingForApprove(Long bookingId, Long userId);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status in (?3) and b.start>?4 " +
            "order by b.start asc")
    List<BookingShort> getNextBooking(Long itemId, Long userId, Set<BookingStatus> statuses,
                                      LocalDateTime now, Pageable pageable);

    @Query("select new ru.practicum.shareit.booking.dto.BookingShort(b.id, b.booker.id) " +
            "from Booking as b " +
            "where b.item.id=?1 and b.item.owner.id=?2 and b.status in (?3) and b.start<?4 " +
            "order by b.start desc")
    List<BookingShort> getLastBooking(Long itemId, Long userId, Set<BookingStatus> statuses,
                                      LocalDateTime now, Pageable pageable);

    // для ALL
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    // для ALL если owner
    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    // для FUTURE
    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    // для FUTURE если owner
    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    // для WAITING
    List<Booking> findAllByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, BookingStatus status);

    // для WAITING если owner
    List<Booking> findAllByItem_OwnerIdAndStatusEqualsOrderByStartDesc(Long ownerId, BookingStatus status);

    // для REJECTED
    List<Booking> findAllByBooker_IdAndStatusInOrderByStartDesc(Long bookerId, Set<BookingStatus> statusSet);

    // для REJECTED если owner
    List<Booking> findAllByItem_OwnerIdAndStatusInOrderByStartDesc(Long ownerId, Set<BookingStatus> statusSet);

    // для CURRENT
    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    // для CURRENT если owner
    List<Booking> findAllByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    // для PAST
    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    // для PAST если owner
    List<Booking> findAllByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);
}
