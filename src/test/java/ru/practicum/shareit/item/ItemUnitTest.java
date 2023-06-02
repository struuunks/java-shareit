package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.InvalidException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.CommentRepository;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.model.Status.APPROVED;

@ExtendWith(SpringExtension.class)
public class ItemUnitTest {

    private static final Integer FROM = 0;
    private static final Integer SIZE = 10;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemValidator validator;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void createItemTest() {
        User user = createUser(1L, "User", "user@yandex.ru");

        ItemRequest itemRequest = ItemRequest.builder()
                .id(2L)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(user)
                .requestId(itemRequest.getId())
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(createItemWithId(itemDto, 1L));

        ItemDto result = itemService.createItem(itemDto, user.getId());

        verify(validator).validate(itemDto, user.getId());
        verify(userRepository).findById(user.getId());
        verify(requestRepository).findById(itemRequest.getId());
        verify(itemRepository).save(any(Item.class));

        assertNotNull(result.getId());
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getOwner().getId(), result.getOwner().getId());
        assertEquals(itemDto.getRequestId(), result.getRequestId());
    }

    @Test
    void updateItemTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        Item item = createItem(2L, "Item", "Description", true, user, new ItemRequest());
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updatedItem = itemService.updateItem(item.getId(), itemDto, user.getId());

        verify(itemRepository).findById(item.getId());
        verify(itemRepository).save(any(Item.class));

        assertEquals(itemDto.getId(), updatedItem.getId());
        assertEquals(itemDto.getName(), updatedItem.getName());
        assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        assertEquals(itemDto.getAvailable(), updatedItem.getAvailable());
    }


    @Test
    void getItemByIdTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        Item item = createItem(2L, "Item", "Description", true, user, new ItemRequest());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOwner retrievedItem = itemService.getItemById(item.getId(), user.getId());

        verify(itemRepository).findById(item.getId());

        assertEquals(item.getId(), retrievedItem.getId());
        assertEquals(item.getName(), retrievedItem.getName());
        assertEquals(item.getDescription(), retrievedItem.getDescription());
        assertEquals(item.getAvailable(), retrievedItem.getAvailable());
    }

    @Test
    void commentWrongItemTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        Item item = createItem(1L, "Item", "Description", true, user, new ItemRequest());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .build();

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.commentItem(item.getId(), commentDto, user.getId()));

        assertEquals("Вещь с айди 1 не найдена", exception.getMessage());

        verify(itemRepository).findById(item.getId());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void commentItemWrongUserTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        Item item = createItem(1L, "Item", "Description", true, user, new ItemRequest());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .build();

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> itemService.commentItem(item.getId(), commentDto, user.getId()));

        assertEquals("Пользователь с айди 1 не найден", exception.getMessage());

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verifyNoInteractions(commentRepository);
    }

    @Test
    void commentItemWithoutBookingTest() {
        User user = createUser(1L, "User", "user@yandex.ru");
        Item item = createItem(1L, "Item", "Description", true, user, new ItemRequest());

        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .build();

        List<Booking> bookings = new ArrayList<>();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingsByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(bookings);

        InvalidException exception = assertThrows(InvalidException.class,
                () -> itemService.commentItem(item.getId(), commentDto, user.getId()));

        assertEquals("Пользователь с айди 1 не бронировал вещь с айди 1", exception.getMessage());

        verify(itemRepository).findById(item.getId());
        verify(userRepository).findById(user.getId());
        verify(bookingRepository).findBookingsByBookerIdAndItemId(user.getId(), item.getId());
        verifyNoInteractions(commentRepository);
    }

    @Test
    void viewAllItemsTest() {
        List<Booking> lastBookings = new ArrayList<>();
        List<Booking> nextBookings = new ArrayList<>();

        User owner = createUser(1L, "User", "user@yandex.ru");
        Item item1 = createItem(1L, "Item1", "Description1", true,
                owner, new ItemRequest());
        Item item2 = createItem(2L, "Item2", "Description2", true,
                owner, new ItemRequest());

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(bookingRepository.findBookingsByStatusAndEndIsBeforeOrderByStartDesc(eq(APPROVED),
                any(LocalDateTime.class))).thenReturn(lastBookings);
        when(bookingRepository.findBookingsByStatusAndStartIsAfterOrderByStartAsc(eq(APPROVED),
                any(LocalDateTime.class))).thenReturn(nextBookings);
        when(itemRepository.findByOwnerIdOrderByIdAsc(eq(1L), any(PageRequest.class))).thenReturn(items);

        List<ItemDtoOwner> result = itemService.viewAllItems(owner.getId(), FROM, SIZE);

        verify(bookingRepository).findBookingsByStatusAndEndIsBeforeOrderByStartDesc(eq(APPROVED),
                any(LocalDateTime.class));
        verify(bookingRepository).findBookingsByStatusAndStartIsAfterOrderByStartAsc(eq(APPROVED),
                any(LocalDateTime.class));
        verify(itemRepository).findByOwnerIdOrderByIdAsc(eq(1L), any(PageRequest.class));

        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item1.getDescription(), result.get(0).getDescription());
        assertNull(result.get(0).getLastBooking());
        assertNull(result.get(0).getNextBooking());

        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item2.getName(), result.get(1).getName());
        assertEquals(item2.getDescription(), result.get(1).getDescription());
        assertNull(result.get(1).getLastBooking());
        assertNull(result.get(1).getNextBooking());
    }

    @Test
    void searchItemsTest() {
        String searchText = "Text";

        Item item1 = createItem(1L, "Item1", "Description1", true,
                new User(), new ItemRequest());
        Item item2 = createItem(2L, "Item2", "Description2", true,
                new User(), new ItemRequest());

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.search(searchText, PageRequest.of(FROM / SIZE, SIZE))).thenReturn(items);

        List<ItemDto> result = itemService.searchItems(searchText, FROM, SIZE);

        verify(itemRepository).search(searchText, PageRequest.of(FROM / SIZE, SIZE));

        assertEquals(2, result.size());
        assertEquals(items.get(0).getId(), result.get(0).getId());
        assertEquals(items.get(0).getName(), result.get(0).getName());
        assertEquals(items.get(0).getDescription(), result.get(0).getDescription());

        assertEquals(items.get(1).getId(), result.get(1).getId());
        assertEquals(items.get(1).getName(), result.get(1).getName());
        assertEquals(items.get(1).getDescription(), result.get(1).getDescription());
    }

    @Test
    void searchItemsBlankTextTest() {
        List<ItemDto> result = itemService.searchItems("", FROM, SIZE);

        verifyNoInteractions(itemRepository);

        assertTrue(result.isEmpty());
    }

    private Item createItemWithId(ItemDto itemDto, Long itemId) {
        User owner = itemDto.getOwner();
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = ItemRequest.builder()
                    .id(itemDto.getRequestId())
                    .build();
        }

        return createItem(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemRequest
        );
    }

    private User createUser(Long id, String name, String email) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }

    private Item createItem(Long id,
                            String name,
                            String description,
                            Boolean available,
                            User owner,
                            ItemRequest itemRequest) {
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .owner(owner)
                .request(itemRequest)
                .build();
    }
}