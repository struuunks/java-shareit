package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByStatusAndStartIsAfterOrderByStartAsc(Status status, LocalDateTime ldt);

    List<Booking> findBookingsByStatusAndEndIsBeforeOrderByStartDesc(Status status, LocalDateTime ldt);

    List<Booking> findBookingsByItemOwnerIdOrderByIdDesc(Long ownerId, Pageable pageable);

    List<Booking> findBookingsByBookerIdOrderByIdDesc(Long bookerId, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndItemId(Long bookerId, Long itemId);

    Booking findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(Long itemId, LocalDateTime ldt, Status status);

    Booking findTopByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(Long itemId, LocalDateTime ldt, Status status);
}
