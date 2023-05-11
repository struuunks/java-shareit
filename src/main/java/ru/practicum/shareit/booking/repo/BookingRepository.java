package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByItemOwnerId(Long ownerId);

    List<Booking> findBookingsByBookerId(Long bookerId);

    List<Booking> findBookingsByBookerIdAndItemId(Long bookerId, Long itemId);

    Booking findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(Long itemId, LocalDateTime ldt, Status status);

    Booking findTopByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(Long itemId, LocalDateTime ldt, Status status);
}
