package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.server.booking.dto.BookingDtoReceived;
import ru.practicum.shareit.server.booking.dto.BookingDtoReturned;
import ru.practicum.shareit.server.booking.mapper.BookingMapper;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.repo.BookingRepository;
import ru.practicum.shareit.server.booking.service.BookingServiceImpl;
import ru.practicum.shareit.server.exception.DataNotFoundException;
import ru.practicum.shareit.server.exception.InvalidException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repo.ItemRepository;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.server.booking.model.Status.APPROVED;
import static ru.practicum.shareit.server.booking.model.Status.WAITING;

@ExtendWith(SpringExtension.class)
public class BookingUnitTest {

    private static final Integer FROM = 0;
    private static final Integer SIZE = 10;
    private static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingTest() {
        User owner = createUser(1L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);
        Optional<Item> ofResult = Optional.of(item);
        when(itemRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User user = createUser(1L, "User", "user@yandex.ru");
        Optional<User> ofResult2 = Optional.of(user);
        when(userRepository.findById(Mockito.<Long>any())).thenReturn(ofResult2);

        BookingDtoReceived bookingDto = mock(BookingDtoReceived.class);

        assertThrows(DataNotFoundException.class, () -> bookingService.createBooking(bookingDto, user.getId()));

        verify(itemRepository).findById(Mockito.<Long>any());
        verify(userRepository).findById(Mockito.<Long>any());
        verify(bookingDto).getItemId();
    }

    @Test
    void bookingConfirmationTest() {
        User owner = createUser(1L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item, new User(), WAITING
        );
        BookingDtoReturned bookingDto = BookingMapper.toBookingDtoReturned(booking);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(booking.getId())).thenReturn(Optional.of(booking.getItem().getOwner()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingDtoReturned result = bookingService.bookingConfirmation(booking.getId(), true, owner.getId());
        result.getStart().format(DateTimeFormatter.ofPattern(TIME_PATTERN));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(bookingDto.getItem(), result.getItem());
        assertEquals(bookingDto.getBooker(), result.getBooker());
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void wrongBookingConfirmationTest() {
        User owner = createUser(1L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item, new User(), WAITING
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                bookingService.bookingConfirmation(booking.getId(), true, owner.getId()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void bookingConfirmationWrongUserTest() {
        User owner = createUser(2L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item, new User(), APPROVED
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                bookingService.bookingConfirmation(booking.getId(), true, owner.getId()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void bookingConfirmationDoubleApprovedTest() {
        User owner = createUser(2L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), item, new User(), APPROVED
        );

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(booking.getItem().getOwner()));

        assertThrows(InvalidException.class, () ->
                bookingService.bookingConfirmation(booking.getId(), true, owner.getId()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void getBookingByIdTest() {
        User owner = createUser(2L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        User booker = createUser(2L, "Booker", "booker@yandex.ru");

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, WAITING
        );
        BookingDtoReturned bookingDto = BookingMapper.toBookingDtoReturned(booking);

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booking.getBooker()));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDtoReturned result = bookingService.getBookingById(booking.getId(), booker.getId());

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findById(eq(booking.getId()));

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(bookingDto.getItem(), result.getItem());
        assertEquals(bookingDto.getBooker(), result.getBooker());
        assertEquals(bookingDto.getStatus(), result.getStatus());
    }

    @Test
    public void getBookingByIdWrongUserTest() {
        User owner = createUser(2L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        User booker = createUser(2L, "Booker", "booker@yandex.ru");

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, WAITING
        );

        when(userRepository.findById(booker.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingById(booking.getId(), booker.getId()));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void getBookingByWrongIdTest() {
        User owner = createUser(2L, "Owner", "owner@yandex.ru");

        Item item = createItem(1L, "Item", "Description", owner, new ItemRequest(), true);

        User booker = createUser(2L, "Booker", "booker@yandex.ru");

        Booking booking = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, WAITING
        );

        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingById(booking.getId(), booker.getId()));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
    }

    @Test
    public void getAllBookingsByUserTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner1 = createUser(1L, "Owner", "owner@yandex.ru");
        User owner2 = createUser(1L, "Owner2", "owner2@yandex.ru");

        Booking booking1 = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), new Item(), owner1, WAITING
        );
        booking1.getItem().setId(1L);
        BookingDtoReturned bookingDto1 = BookingMapper.toBookingDtoReturned(booking1);

        Booking booking2 = createBooking(
                2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), new Item(), owner2, APPROVED
        );
        booking2.getItem().setId(2L);
        BookingDtoReturned bookingDto2 = BookingMapper.toBookingDtoReturned(booking2);

        List<Booking> bookingsDB = new ArrayList<>();
        bookingsDB.add(booking1);
        bookingsDB.add(booking2);

        when(bookingRepository.findBookingsByBookerIdOrderByIdDesc(eq(1L),
                any(PageRequest.class))).thenReturn(bookingsDB);

        List<BookingDtoReturned> result = bookingService.getAllBookingsByUser("ALL", user.getId(), FROM, SIZE);

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(bookingRepository,
                times(1)).findBookingsByBookerIdOrderByIdDesc(eq(1L),
                eq(PageRequest.of(0, SIZE)));

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(bookingDto1.getId(), result.get(1).getId());
        assertEquals(bookingDto1.getStart(), result.get(1).getStart());
        assertEquals(bookingDto1.getEnd(), result.get(1).getEnd());
        assertEquals(bookingDto1.getItem(), result.get(1).getItem());
        assertEquals(bookingDto1.getBooker(), result.get(1).getBooker());
        assertEquals(bookingDto1.getStatus(), result.get(1).getStatus());

        assertEquals(bookingDto2.getId(), result.get(0).getId());
        assertEquals(bookingDto2.getStart(), result.get(0).getStart());
        assertEquals(bookingDto2.getEnd(), result.get(0).getEnd());
        assertEquals(bookingDto2.getItem(), result.get(0).getItem());
        assertEquals(bookingDto2.getBooker(), result.get(0).getBooker());
        assertEquals(bookingDto2.getStatus(), result.get(0).getStatus());
    }

    @Test
    public void getAllBookingsByOwnerTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User owner1 = createUser(1L, "Owner", "owner@yandex.ru");
        User owner2 = createUser(1L, "Owner2", "owner2@yandex.ru");

        Booking booking1 = createBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), new Item(), owner1, WAITING
        );
        booking1.getItem().setId(1L);
        BookingDtoReturned bookingDto1 = BookingMapper.toBookingDtoReturned(booking1);

        Booking booking2 = createBooking(
                2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), new Item(), owner2, APPROVED
        );
        booking2.getItem().setId(2L);
        BookingDtoReturned bookingDto2 = BookingMapper.toBookingDtoReturned(booking2);

        List<Booking> bookingsDB = new ArrayList<>();
        bookingsDB.add(booking1);
        bookingsDB.add(booking2);

        when(bookingRepository.findBookingsByItemOwnerIdOrderByIdDesc(eq(1L),
                any(PageRequest.class))).thenReturn(bookingsDB);

        List<BookingDtoReturned> result = bookingService.getAllBookingsByOwner("ALL", user.getId(), FROM, SIZE);

        verify(userRepository, times(1)).findById(eq(1L));
        verify(bookingRepository, times(1)).findBookingsByItemOwnerIdOrderByIdDesc(eq(1L),
                eq(PageRequest.of(0, SIZE)));

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(bookingDto1.getId(), result.get(1).getId());
        assertEquals(bookingDto1.getStart(), result.get(1).getStart());
        assertEquals(bookingDto1.getEnd(), result.get(1).getEnd());
        assertEquals(bookingDto1.getItem(), result.get(1).getItem());
        assertEquals(bookingDto1.getBooker(), result.get(1).getBooker());
        assertEquals(bookingDto1.getStatus(), result.get(1).getStatus());

        assertEquals(bookingDto2.getId(), result.get(0).getId());
        assertEquals(bookingDto2.getStart(), result.get(0).getStart());
        assertEquals(bookingDto2.getEnd(), result.get(0).getEnd());
        assertEquals(bookingDto2.getItem(), result.get(0).getItem());
        assertEquals(bookingDto2.getBooker(), result.get(0).getBooker());
        assertEquals(bookingDto2.getStatus(), result.get(0).getStatus());
    }

    private User createUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private Item createItem(Long id, String name, String description, User owner, ItemRequest itemRequest,
                            Boolean available) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .owner(owner)
                .request(itemRequest)
                .available(available)
                .build();
    }

    private Booking createBooking(Long id,
                                  LocalDateTime start,
                                  LocalDateTime end,
                                  Item item,
                                  User booker,
                                  Status status) {
        return Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}
