package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "where b.id=?1 and( b.booker.id=?2 or b.item.owner.id=?2 )")
    Optional<Booking> findBookingByOwnerOrBooker(Long id, Long finderId);

    @Query("select b from Booking as b where b.id=?1 and b.item.owner.id=?2")
    Optional<Booking> findBookingForApprove(Long bookingId, Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_OwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime dateTime);

    List<Booking> findAllByItem_OwnerId(Long ownerId);
}
