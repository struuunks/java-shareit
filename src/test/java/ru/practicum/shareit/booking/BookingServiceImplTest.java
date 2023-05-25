package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.exception.UnsupportedStateException;
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

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.Status.*;

@SpringBootTest
@Transactional
public class BookingServiceImplTest {

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
    void createBookingWithEndTimeEndsEarlierStartTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setEnd(LocalDateTime.now().plusMinutes(20));

        assertThrows(InvalidException.class,
                () -> bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId()));
    }

    @Test
    void createBookingWithoutStartTimeTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReceived bookingDtoReceived = BookingMapper.toBookingDtoReceived(booking);
        bookingDtoReceived.setStart(null);

        assertThrows(InvalidException.class,
                () -> bookingService.createBooking(bookingDtoReceived, booker.getId()));
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
    void bookingRejectedConfirmationTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        bookingDtoReturned = bookingService.bookingConfirmation(booking.getId(), false, user.getId());

        assertEquals(bookingDtoReturned.getStatus(), REJECTED);
    }

    @Test
    void bookingDoubleConfirmationTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        bookingService.bookingConfirmation(booking.getId(), true, user.getId());

        assertThrows(InvalidException.class,
                () -> bookingService.bookingConfirmation(booking.getId(), true, user.getId()));
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
    void getBookingByWrongIdTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookingById(1L, user.getId()));
    }

    @Test
    void getBookingByIdUserTest(){
        assertThrows(DataNotFoundException.class,
                () -> bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), 6L));
    }

    @Test
    void getBookingByIdWrongUserTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        User otherUser = new User(null, "other", "other@yandex.ru");
        UserDto otherDto = userService.createUser(UserMapper.toUserDto(otherUser));
        otherUser.setId(otherDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), otherUser.getId()));
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
    void getAllBookingsByWrongUserTest(){
        assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllBookingsByUser("ALL", 5L, 2, 10));
    }

    @Test
    void getWrongPaginationBookingsByUserTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(InvalidException.class,
                () -> bookingService.getAllBookingsByUser("ALL", user.getId(), -2, 10));
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

    @Test
    void getWaitingBookingsByOwnerTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStatus(WAITING);
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());
        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("WAITING", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getRejectedBookingsByOwnerTest() {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());
        bookingService.bookingConfirmation(booking.getId(), false, user.getId());
        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("REJECTED", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getPastBookingsByOwnerTest() throws InterruptedException {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStart(LocalDateTime.now().plusSeconds(2));
        booking.setEnd(LocalDateTime.now().plusSeconds(4));
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        sleep(5000);

        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("PAST", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getCurrentBookingsByOwnerTest() throws InterruptedException {
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStart(LocalDateTime.now().plusSeconds(1));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        sleep(1000);

        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("CURRENT", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getFutureBookingsByOwnerTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        UserDto bookerDto = userService.createUser(UserMapper.toUserDto(booker));
        booker.setId(bookerDto.getId());

        ItemDto itemDto = itemService.createItem(ItemMapper.toItemDto(item), userDto.getId());
        item.setId(itemDto.getId());

        booking.setStart(LocalDateTime.now().plusSeconds(30));
        booking.setEnd(LocalDateTime.now().plusHours(1));
        BookingDtoReturned bookingDtoReturned =
                bookingService.createBooking(BookingMapper.toBookingDtoReceived(booking), booker.getId());
        booking.setId(bookingDtoReturned.getId());

        List<BookingDtoReturned> bookings =
                bookingService.getAllBookingsByOwner("FUTURE", user.getId(), 0, 10);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
    }

    @Test
    void getWrongStateBookingsByOwnerTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getAllBookingsByOwner("UNSUPPORTED", user.getId(), 0, 10));
    }

    @Test
    void getWrongPaginationBookingsByOwnerTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(InvalidException.class,
                () -> bookingService.getAllBookingsByOwner("ALL", user.getId(), -2, 10));
    }

    @Test
    void getBookingsByWrongOwnerTest(){
        UserDto userDto = userService.createUser(UserMapper.toUserDto(user));
        user.setId(userDto.getId());

        assertThrows(DataNotFoundException.class,
                () -> bookingService.getAllBookingsByOwner("ALL", 3L, 2, 10));
    }
}
