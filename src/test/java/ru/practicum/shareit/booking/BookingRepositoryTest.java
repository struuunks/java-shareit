package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindBookingsByStatusAndStartIsAfterOrderByStartAsc() {

        User booker = new User(1L, "user", "user@yandex.ru");
        userRepository.save(booker);

        Item item = new Item(1L, "item", "description", true, booker, null);
        itemRepository.save(item);

        LocalDateTime currentTime = LocalDateTime.now();

        Booking booking1 = new Booking(1L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusHours(1),
                item, booker, Status.APPROVED);
        bookingRepository.save(booking1);

        Booking booking2 = new Booking(2L, LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(4), item, booker, Status.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> foundBookings = bookingRepository
                .findBookingsByStatusAndStartIsAfterOrderByStartAsc(Status.APPROVED, currentTime);

        assertEquals(2, foundBookings.size());

        assertEquals(booking1.getId(), foundBookings.get(0).getId());
        assertEquals(booking1.getItem().getId(), foundBookings.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), foundBookings.get(0).getBooker().getId());

        assertEquals(booking2.getId(), foundBookings.get(1).getId());
        assertEquals(booking2.getItem().getId(), foundBookings.get(1).getItem().getId());
        assertEquals(booking2.getBooker().getId(), foundBookings.get(1).getBooker().getId());
    }
}
