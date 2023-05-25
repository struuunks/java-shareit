package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@SpringBootTest
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    private final User user = new User(null, "user", "user@yandex.ru");
    private final User booker = new User(null, "booker", "booker@yandex.ru");
    private final Item item = new Item(null, "item", "description", true, user, null);
    private final Booking booking = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
            item, user, WAITING);

    @Test
    void createBookingTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        assertEquals(bookingDtoReturned.getId(), booking.getId());
        assertEquals(bookingDtoReturned.getStatus(), booking.getStatus());
        assertEquals(bookingDtoReturned.getBooker().getId(), booker.getId());
    }

    @Test
    void createBookingWithWrongStartTimeTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStart(LocalDateTime.now().minusHours(1));

        assertThrows(InvalidException.class,
                () -> bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId()));
    }

    @Test
    void createBookingByOwnerTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), user.getId()));
    }

    @Test
    void createBookingWithNotAvailableItemTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        item.setAvailable(false);
        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStart(LocalDateTime.now().minusHours(1));

        assertThrows(InvalidException.class,
                () -> bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId()));
    }

    @Test
    void bookingConfirmationTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        bookingDtoReturned = bookingService.bookingConfirmation(booking.getId(), true, user.getId());

        assertEquals(bookingDtoReturned.getStatus(), Status.APPROVED);
    }

    @Test
    void getBookingByIdTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());
        bookingDtoReturned = bookingService.getBookingById(booking.getId(), user.getId());

        assertEquals(bookingDtoReturned.getId(), booking.getId());
        assertEquals(bookingDtoReturned.getItem().getName(), item.getName());
        assertEquals(bookingDtoReturned.getBooker().getId(), booker.getId());
    }

    @Test
    void getAllBookingsByUserTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());
        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByUser("ALL", booker.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getAllBookingsByOwnerTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());
        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("ALL", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }
}
