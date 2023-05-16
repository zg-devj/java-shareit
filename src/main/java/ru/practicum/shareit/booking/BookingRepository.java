package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "where b.id=?1 and( b.booker.id=?2 or b.item.owner.id=?2 )")
    Optional<Booking> findBookingByOwnerOrBooker(Long id, Long finderId);
}
